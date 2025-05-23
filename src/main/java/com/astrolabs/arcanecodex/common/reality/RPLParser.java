package com.astrolabs.arcanecodex.common.reality;

import com.astrolabs.arcanecodex.common.reality.commands.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RPLParser {
    
    private static final Map<String, CommandFactory> COMMANDS = new HashMap<>();
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("(\\w+)\\.(\\w+)\\((.*?)\\)");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(\\w+):\\s*([^,}]+)");
    
    static {
        // Register base commands
        COMMANDS.put("reality.manifest", GravityCommand::parse);
        COMMANDS.put("player.augment", PhaseShiftCommand::parse);
        COMMANDS.put("quantum.measure", QuantumMeasureCommand::parse);
        COMMANDS.put("time.dilate", TimeDilationCommand::parse);
        COMMANDS.put("energy.cascade", EnergyCascadeCommand::parse);
    }
    
    public static RPLCommand parse(String code) throws RPLException {
        // Remove comments and extra whitespace
        code = code.replaceAll("//.*", "").trim();
        
        // Match function pattern
        Matcher functionMatcher = FUNCTION_PATTERN.matcher(code);
        if (!functionMatcher.find()) {
            throw new RPLException("Invalid RPL syntax: " + code);
        }
        
        String object = functionMatcher.group(1);
        String method = functionMatcher.group(2);
        String parameters = functionMatcher.group(3);
        
        String commandKey = object + "." + method;
        CommandFactory factory = COMMANDS.get(commandKey);
        
        if (factory == null) {
            throw new RPLException("Unknown command: " + commandKey);
        }
        
        // Parse parameters
        Map<String, String> params = parseParameters(parameters);
        
        return factory.create(params);
    }
    
    private static Map<String, String> parseParameters(String paramString) throws RPLException {
        Map<String, String> params = new HashMap<>();
        
        // Handle object notation { key: value, key: value }
        if (paramString.trim().startsWith("{") && paramString.trim().endsWith("}")) {
            paramString = paramString.trim().substring(1, paramString.length() - 1);
            
            Matcher paramMatcher = PARAMETER_PATTERN.matcher(paramString);
            while (paramMatcher.find()) {
                String key = paramMatcher.group(1);
                String value = paramMatcher.group(2).trim();
                
                // Remove quotes if present
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                
                params.put(key, value);
            }
        } else if (!paramString.trim().isEmpty()) {
            // Handle simple parameters
            String[] parts = paramString.split(",");
            for (int i = 0; i < parts.length; i++) {
                params.put("arg" + i, parts[i].trim());
            }
        }
        
        return params;
    }
    
    public interface CommandFactory {
        RPLCommand create(Map<String, String> parameters) throws RPLException;
    }
    
    public static abstract class RPLCommand {
        protected final Map<String, String> parameters;
        
        public RPLCommand(Map<String, String> parameters) {
            this.parameters = parameters;
        }
        
        public abstract void execute(Level level, Player player, BlockPos pos) throws RPLException;
        
        public abstract int getEnergyCost();
        
        protected String getRequiredParam(String key) throws RPLException {
            String value = parameters.get(key);
            if (value == null) {
                throw new RPLException("Missing required parameter: " + key);
            }
            return value;
        }
        
        protected String getParam(String key, String defaultValue) {
            return parameters.getOrDefault(key, defaultValue);
        }
        
        protected int getIntParam(String key, int defaultValue) {
            try {
                return Integer.parseInt(parameters.getOrDefault(key, String.valueOf(defaultValue)));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        
        protected double getDoubleParam(String key, double defaultValue) {
            try {
                return Double.parseDouble(parameters.getOrDefault(key, String.valueOf(defaultValue)));
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }
    
    public static class RPLException extends Exception {
        public RPLException(String message) {
            super(message);
        }
    }
}