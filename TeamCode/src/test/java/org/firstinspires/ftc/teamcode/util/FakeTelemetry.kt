package org.firstinspires.ftc.teamcode.util

import org.firstinspires.ftc.robotcore.external.Func
import org.firstinspires.ftc.robotcore.external.Telemetry
import java.lang.Runnable
import java.lang.reflect.Proxy

class FakeTelemetry : Telemetry {
    private val dataMap = mutableMapOf<String, String>()
    private val linesList = mutableListOf<String>()

    val loggedData: Map<String, String> get() = dataMap
    val loggedLines: List<String> get() = linesList

    private val dummyItem = Proxy.newProxyInstance(
        Telemetry.Item::class.java.classLoader,
        arrayOf(Telemetry.Item::class.java)
    ) { proxy, method, args ->
        when (method.name) {
            "hashCode" -> System.identityHashCode(proxy)
            "equals" -> proxy === args?.getOrNull(0)
            "toString" -> "DummyItem"
            else -> proxy
        }
    } as Telemetry.Item

    private val dummyLine = Proxy.newProxyInstance(
        Telemetry.Line::class.java.classLoader,
        arrayOf(Telemetry.Line::class.java)
    ) { proxy, method, args ->
        when (method.name) {
            "hashCode" -> System.identityHashCode(proxy)
            "equals" -> proxy === args?.getOrNull(0)
            "toString" -> "DummyLine"
            else -> proxy
        }
    } as Telemetry.Line

    override fun addData(caption: String?, value: Any?): Telemetry.Item {
        if (caption != null) {
            dataMap[caption] = value?.toString() ?: "null"
        }
        return dummyItem
    }

    override fun addData(caption: String?, format: String?, vararg args: Any?): Telemetry.Item {
        val formatted = if (args.isNotEmpty()) String.format(format ?: "", *args) else format
        if (caption != null) {
            dataMap[caption] = formatted ?: "null"
        }
        return dummyItem
    }

    override fun <T : Any?> addData(caption: String?, value: Func<T>?): Telemetry.Item {
        if (caption != null && value != null) {
            dataMap[caption] = value.value()?.toString() ?: "null"
        }
        return dummyItem
    }

    override fun <T : Any?> addData(caption: String?, format: String?, value: Func<T>?): Telemetry.Item {
        if (caption != null && value != null) {
            dataMap[caption] = String.format(format ?: "", value.value())
        }
        return dummyItem
    }

    override fun addLine(): Telemetry.Line {
        linesList.add("")
        return dummyLine
    }

    override fun addLine(lineCaption: String?): Telemetry.Line {
        if (lineCaption != null) {
            linesList.add(lineCaption)
        }
        return dummyLine
    }

    override fun removeItem(item: Telemetry.Item?): Boolean = true
    override fun removeLine(line: Telemetry.Line?): Boolean = true
    override fun clear() {
        dataMap.clear()
        linesList.clear()
    }
    override fun clearAll() {
        clear()
    }
    override fun update(): Boolean = true
    override fun setMsTransmissionInterval(ms: Int) {}
    override fun getMsTransmissionInterval(): Int = 50
    override fun setAutoClear(autoClear: Boolean) {}
    override fun isAutoClear(): Boolean = false
    override fun setDisplayFormat(displayFormat: Telemetry.DisplayFormat?) {}
    override fun speak(text: String?) {}
    override fun speak(text: String?, languageCode: String?, countryCode: String?) {}
    override fun log(): Telemetry.Log = null!!
    override fun addAction(action: Runnable?): Any = null!!
    override fun removeAction(action: Any?): Boolean = true
    override fun getItemSeparator(): String = ": "
    override fun setItemSeparator(separator: String?) {}
    override fun getCaptionValueSeparator(): String = "="
    override fun setCaptionValueSeparator(separator: String?) {}
}
