package by.chemerisuk.cordova.support;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;


public class CordovaPlugin extends org.apache.cordova.CordovaPlugin {
    private Map<String, CordovaMethodCommand> methodsMap;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (methodsMap == null) {
            methodsMap = new HashMap<String, CordovaMethodCommand>();
            for (Method method : this.getClass().getDeclaredMethods()) {
                CordovaMethod cordovaMethod = method.getAnnotation(CordovaMethod.class);
                if (cordovaMethod != null) {
                    String methodAction = cordovaMethod.action();
                    if (methodAction.isEmpty()) {
                        methodAction = method.getName();
                    }
                    methodsMap.put(methodAction, new CordovaMethodCommand(
                        this, method, cordovaMethod.async()));

                    try {
                        // suppress Java language access checks
                        // to improve performance of future calls
                        method.setAccessible(true);
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        CordovaMethodCommand command = methodsMap.get(action);
        if (command != null) {
            command.init(args, callbackContext);
            if (command.isAsync()) {
                cordova.getThreadPool().execute(command);
            } else {
                command.run();
            }
            return true;
        }

        return false;
    }

    private static class CordovaMethodCommand implements Runnable {
        private final CordovaPlugin plugin;
        private final Method method;
        private final boolean async;
        private Object[] methodArgs;

        public CordovaMethodCommand(CordovaPlugin plugin, Method method, boolean async) {
            this.plugin = plugin;
            this.method = method;
            this.async = async;
        }

        public void init(JSONArray args, CallbackContext callbackContext) throws JSONException {
            Class<?>[] argTypes = method.getParameterTypes();
            this.methodArgs = new Object[argTypes.length];

            for (int i = 0; i < argTypes.length; ++i) {
                Class<?> argType = argTypes[i];
                if (CallbackContext.class.equals(argType)) {
                    this.methodArgs[i] = callbackContext;
                } else if (JSONArray.class.equals(argType)) {
                    this.methodArgs[i] = args.optJSONArray(i);
                } else if (JSONObject.class.equals(argType)) {
                    this.methodArgs[i] = args.optJSONObject(i);
                } else {
                    this.methodArgs[i] = args.get(i);
                }
            }
        }

        @Override
        public void run() {
            try {
                this.method.invoke(this.plugin, this.methodArgs);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            } finally {
                this.methodArgs = null;
            }
        }

        public boolean isAsync() {
            return this.async;
        }
    }
}
