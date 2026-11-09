package org.firstinspires.ftc.teamcode.util

import com.skeletonarmy.marrow.telemetry.MarrowTelemetry
import com.skeletonarmy.marrow.telemetry.TelemetryModifier
import com.skeletonarmy.marrow.telemetry.modifiers.BoldModifier
import dev.nextftc.robot.TelemetryBackend
import org.firstinspires.ftc.robotcore.external.Telemetry


object HtmlTelemetry {
    private val backends: MutableSet<Telemetry> = mutableSetOf()
    private lateinit var marrowTelemetry: MarrowTelemetry

    fun addBackend(telemetry: Telemetry) {
        backends.add(telemetry)
    }

    fun <T> log(string: String, value: T, modifier: TelemetryModifier? = null) {
        for (backend in backends) {
            backend.addData(string, value.toString())
        }
        if (::marrowTelemetry.isInitialized) {
            marrowTelemetry.addData(string, value.toString(), modifier)
        }
    }
    fun <T> log(value: T, modifier: TelemetryModifier? = null) {
        for (backend in backends) {
            backend.addData(value.toString(),"")
        }
        if (::marrowTelemetry.isInitialized) {
            marrowTelemetry.addData(value.toString(), modifier)
        }
    }

    fun update(){
        for (backend in backends) {
            backend.update()
        }
        if (::marrowTelemetry.isInitialized) {
            marrowTelemetry.update()
        }
    }
}