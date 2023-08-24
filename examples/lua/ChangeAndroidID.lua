--[[
    Global variables set by the LuaModule class:
        1) argument (Class)
            This class is used to mark values as ignored or required in exactMatchArguments().
            Methods:
                - require(value): Require a specific value
                - ignore(): Ignore the argument
--]]

--[[
	This is custom Argument Matching Function.
	It MUST return either true of false
--]]
function matchArguments(...)

    local args = {...}

    -- Remember that Lua is 1-indexed!
    if args[2] == "android_id"
    then
        return true
    end

    return false
end

--[[
    This function is called when registering the module to the LuaModuleRegistry class

    WARNING! This function must be present otherwise the module will be discarded
--]]
function registerModule(moduleRegistry)

    --[[
        This example focuses on changing the Android ID returned by Settings$Secure.getString()

        Both exactMatchArguments() and customMatchArgument() in this example work exactly the same.

        The only difference between the two is that customMatchArgument() is MUCH SLOWER than exactMatchArguments()
    --]]
    local contentResolver = argument:ignore()
    local name = argument:require("android_id")
    moduleRegistry:exactMatchArguments(contentResolver, name)

    --[[
        WARNING! customMatchArgument() should be used only as a last resort.
        It is MUCH SLOWER than other look-up methods ( linear time at best ).
    --]]
    moduleRegistry:customMatchArgument(matchArguments)
end

--[[
    This function is called only when matching function was found and all modules before it didn't modified the hooked function return value

    WARNING! This function must be present otherwise the module will be discarded
--]]
function runModule(hookParameters)
    -- TODO: Check if these assignments work
    hookParameters.result = "Fake Android ID"
    hookParameters:setResult("Fake Android ID")
end
