package org.firstinspires.ftc.teamcode.util

import com.skeletonarmy.marrow.telemetry.FormatBuilder
import com.skeletonarmy.marrow.telemetry.MarrowTelemetry
import com.skeletonarmy.marrow.telemetry.TelemetryModifier
import org.firstinspires.ftc.robotcore.external.Telemetry

object HtmlTelemetry {
    private val backends: MutableSet<Telemetry> = mutableSetOf()
    private val marrowTelemetry by lazy { MarrowTelemetry(MyOpModeManager.telemetry) }

    fun addBackend(telemetry: Telemetry) {
        backends.add(telemetry)
    }

    fun addData(caption: String, value: Any?, vararg modifiers: TelemetryModifier) {
        val strValue = value?.toString() ?: "null"
        for (backend in backends) {
            backend.addData(caption, strValue)
        }
        marrowTelemetry.addData(caption, strValue, *modifiers)
    }

    fun addData(caption: String, formatBuilder: FormatBuilder) {
        val formatted = formatBuilder.format()
        for (backend in backends) {
            backend.addData(caption, formatted)
        }
        marrowTelemetry.addData(caption, formatted)
    }

    fun addLine(line: Any?, vararg modifiers: TelemetryModifier) {
        val strLine = line?.toString() ?: ""
        for (backend in backends) {
            backend.addLine(strLine)
        }
        marrowTelemetry.addData(null, strLine, *modifiers)
    }

    fun addLine(formatBuilder: FormatBuilder) {
        val formatted = formatBuilder.format()
        for (backend in backends) {
            backend.addLine(formatted)
        }
        marrowTelemetry.addData(null, formatted)
    }

    fun log(caption: String, value: Any?, vararg modifiers: TelemetryModifier) {
        addData(caption, value, *modifiers)
    }

    fun log(line: Any?) {
        addLine(line)
    }

    fun log(line: String, modifier: TelemetryModifier, vararg extraModifiers: TelemetryModifier) {
        addLine(line, modifier, *extraModifiers)
    }

    fun update() {
        for (backend in backends) {
            backend.update()
        }
        marrowTelemetry.update()
    }
}