package com.ltei.datainterface.processor.generator

import com.ltei.datainterface.processor.model.GenerationConfig

object GenerationUtils {
    fun buildValuesNullableCheckString(model: GenerationConfig.Model): String {
        val builder = StringBuilder()
        for ((idx, value) in model.values.withIndex()) {
            builder.append(value.name).append(" != null")
            if (idx < model.values.lastIndex) builder.append(" && ")
        }
        return builder.toString()
    }

    fun buildValuesParamString(config: GenerationConfig, model: GenerationConfig.Model, toMutable: Boolean): String {
        val builder = StringBuilder()
        for ((idx, value) in model.values.withIndex()) {
            if (toMutable)
                builder.append(config.buildCastToMutableStatement(value.name, value.type))
            else
                builder.append(value.name)
            if (idx < model.values.lastIndex) builder.append(", ")
        }
        return builder.toString()
    }

    fun buildValuesNotNullParamString(model: GenerationConfig.Model): String {
        val builder = StringBuilder()
        for ((idx, value) in model.values.withIndex()) {
            builder.append(value.name).append("!!")
            if (idx < model.values.lastIndex) builder.append(", ")
        }
        return builder.toString()
    }
}