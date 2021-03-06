package posidon.texter.ui.view

import posidon.texter.Window
import posidon.texter.backend.Settings
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel

open class SettingsView(
    label: String,
    val key: String,
    val type: Settings.Type
) : JComponent() {

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        add(JLabel(label).apply {
            foreground = Window.theme.text
            isFocusable = false
            border = BorderFactory.createEmptyBorder(0, 0, 0, 18)
        })
    }

    protected fun save(value: Any) {
        when (type) {
            Settings.Type.TEXT -> Settings[key] = value as String
            Settings.Type.INT -> Settings[key] = value as Int
            Settings.Type.FLOAT -> Settings[key] = value as Float
            Settings.Type.BOOL -> Settings[key] = value as Boolean
            Settings.Type.LIST -> if (value is Array<*>) when {
                value.isArrayOf<Int>() -> Settings[key] = value as Array<Int>
                value.isArrayOf<Float>() -> Settings[key] = value as Array<Float>
                value.isArrayOf<Boolean>() -> Settings[key] = value as Array<Boolean>
            }
        }
        Settings.apply()
    }
}