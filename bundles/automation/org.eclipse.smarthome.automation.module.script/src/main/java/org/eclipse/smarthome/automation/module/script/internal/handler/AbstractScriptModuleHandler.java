/**
 * Copyright (c) 1997, 2015 by ProSyst Software GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.automation.module.script.internal.handler;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.eclipse.smarthome.automation.Module;
import org.eclipse.smarthome.automation.handler.BaseModuleHandler;
import org.eclipse.smarthome.automation.module.script.ScriptEngineContainer;
import org.eclipse.smarthome.automation.module.script.ScriptEngineManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is an abstract class that can be used when implementing any module handler that handles scripts.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Simon Merschjohann
 *
 * @param <T> the type of module the concrete handler can handle
 */
abstract public class AbstractScriptModuleHandler<T extends Module> extends BaseModuleHandler<T> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** Constant defining the configuration parameter of modules that specifies the mime type of a script */
    protected static final String SCRIPT_TYPE = "type";

    /** Constant defining the configuration parameter of modules that specifies the script itself */
    protected static final String SCRIPT = "script";

    protected ScriptEngineManager scriptEngineManager;

    private String engineIdentifier;

    private Optional<ScriptEngine> scriptEngine = Optional.empty();
    private String type;
    protected String script;

    private String ruleUID;

    public AbstractScriptModuleHandler(T module, String ruleUID, ScriptEngineManager scriptEngineManager) {
        super(module);
        this.scriptEngineManager = scriptEngineManager;
        this.ruleUID = ruleUID;
        engineIdentifier = UUID.randomUUID().toString();

        loadConfig();
    }

    @Override
    public void dispose() {
        if (scriptEngine != null) {
            scriptEngineManager.removeEngine(engineIdentifier);
        }
    }

    protected Optional<ScriptEngine> getScriptEngine() {
        return scriptEngine.isPresent() ? scriptEngine : createScriptEngine();
    }

    private Optional<ScriptEngine> createScriptEngine() {
        ScriptEngineContainer container = scriptEngineManager.createScriptEngine(type, engineIdentifier);

        if (container != null) {
            scriptEngine = Optional.ofNullable(container.getScriptEngine());
            return scriptEngine;
        } else {
            logger.debug("No engine available for script type '{}' in action '{}'.", type, module.getId());
            return Optional.empty();
        }
    }

    private void loadConfig() {
        Object type = module.getConfiguration().get(SCRIPT_TYPE);
        Object script = module.getConfiguration().get(SCRIPT);
        if (type == null || !(type instanceof String) || ((String) type).trim().isEmpty()) {
            throw new RuntimeException(
                    String.format("Type is missing in the configuration of module '%s'.", module.getId()));
        } else if (script == null || !(script instanceof String) || ((String) script).trim().isEmpty()) {
            throw new RuntimeException(
                    String.format("Script is missing in the configuration of module '%s'.", module.getId()));
        } else {
            this.type = (String) type;
            this.script = (String) script;
        }
    }

    /**
     * Adds the passed context variables of the rule engine to the context scope of the ScriptEngine, this should be
     * updated each time the module is executed
     *
     * @param engine the scriptengine that is used
     * @param context the variables and types to put into the execution context
     */
    protected void setExecutionContext(ScriptEngine engine, Map<String, ?> context) {
        ScriptContext executionContext = engine.getContext();

        // make the whole context available as "ctx"
        // Note: We don't use "context" here as it doesn't work on all JVM versions!
        executionContext.setAttribute("ctx", context, ScriptContext.ENGINE_SCOPE);
        executionContext.setAttribute("ruleUID", this.ruleUID, ScriptContext.ENGINE_SCOPE);

        // add the single context entries without their prefix to the scope
        for (Entry<String, ?> entry : context.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            int dotIndex = key.indexOf('.');
            if (dotIndex != -1) {
                key = key.substring(dotIndex + 1);
            }
            executionContext.setAttribute(key, value, ScriptContext.ENGINE_SCOPE);
        }

    }

}
