package posidon.texter.ui.view

import posidon.texter.Window
import posidon.texter.backend.AnyFile
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.io.File
import java.util.*
import javax.swing.*
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.plaf.basic.BasicTreeUI
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreePath


class FileTree(dir: File? = null) : JPanel() {

    private val scrollPane = JScrollPane()
    private val tree = JTree(if (dir == null) DefaultMutableTreeNode("") else addNodes(null, dir), true)
    private val renderer = Renderer()
    private val treeUI = DecentTreeUI()

    private fun addNodes(curTop: DefaultMutableTreeNode?, dir: File): DefaultMutableTreeNode {
        val curPath = dir.path
        val curDir = curTop ?: DefaultMutableTreeNode(dir.name)
        val ol = Vector<String>()
        val tmp = dir.list()
        if (tmp != null) for (s in tmp) ol.addElement(s)
        ol.sortWith(String.CASE_INSENSITIVE_ORDER)
        val files = Vector<String>()

        for (thisObject in ol) {
            files.addElement(
                if (File(curPath + File.separator + thisObject).isDirectory) thisObject + File.separator
                else thisObject)
        }

        for (i in files.indices) curDir.add(if (files.elementAt(i).endsWith(File.separator)) {
            DefaultMutableTreeNode(files.elementAt(i).substring(0, files.elementAt(i).length - 1), true)
        } else DefaultMutableTreeNode(files.elementAt(i), false))
        //for (i in files.indices) curDir.add(DefaultMutableTreeNode(files.elementAt(i), false))
        return curDir
    }

    fun setFolder(path: String) {
        with(tree.model.root as DefaultMutableTreeNode) {
            removeAllChildren()
            addNodes(this, File(path))
            this.userObject = path
        }
        tree.updateUI()
        tree.setUI(treeUI)
        treeUI.collapsedIcon = null
        treeUI.expandedIcon = null
    }

    fun updateTheme() {
        background = Window.theme.uiBG
        renderer.backgroundSelectionColor = Window.theme.uiHighlight
        renderer.borderSelectionColor = Window.theme.uiBG
        renderer.textSelectionColor = Window.theme.textSelected
        renderer.textNonSelectionColor = Window.theme.text
        renderer.closedIcon = Window.theme.iconTheme.folder
        renderer.openIcon = Window.theme.iconTheme.folder_open
        scrollPane.verticalScrollBar?.setUI(ScrollBarUI())
        scrollPane.horizontalScrollBar?.setUI(ScrollBarUI())
    }

    override fun setBackground(bg: Color?) {
        renderer?.backgroundNonSelectionColor = bg
        scrollPane?.let {
            scrollPane.background = bg
            scrollPane.viewport?.background = bg
        }
    }

    override fun getMinimumSize(): Dimension { return Dimension(200, 400) }
    override fun getPreferredSize(): Dimension { return Dimension(200, 400) }

    init {
        layout = BorderLayout()
        tree.toggleClickCount = 2
        scrollPane.border = null
        scrollPane.viewport.add(tree)
        scrollPane.verticalScrollBar.isOpaque = false
        scrollPane.horizontalScrollBar.isOpaque = false
        add(BorderLayout.CENTER, scrollPane)
        renderer.border = BorderFactory.createEmptyBorder(4, 8, 4, 8)
        tree.cellRenderer = renderer
        tree.isRootVisible = false
        tree.setUI(treeUI)
        treeUI.collapsedIcon = null
        treeUI.expandedIcon = null
        tree.isOpaque = false
        isOpaque = false
        tree.addTreeWillExpandListener(object : TreeWillExpandListener {
            override fun treeWillCollapse(e: TreeExpansionEvent) {}
            override fun treeWillExpand(e: TreeExpansionEvent) {
                addNodes(e.path.lastPathComponent as DefaultMutableTreeNode, File(e.path.path.joinToString(File.separator)))
            }
        })
        tree.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                val row = tree.getRowForLocation(e.x, e.y)
                if (row != -1) {
                    val path: TreePath? = tree.getPathForLocation(e.x, e.y)
                    if (e.button == 1 && e.clickCount == 2 && !(path?.lastPathComponent as MutableTreeNode).allowsChildren) {
                        itemDoubleClickListener?.invoke(path.path.joinToString(File.separator))
                    }
                }
            }
        })
    }

    private var itemDoubleClickListener: ((path: String) -> Unit)? = null

    fun setLeafDoubleClickListener(listener: (path: String) -> Unit) {
        itemDoubleClickListener = listener
    }

    fun addSelectionListener(listener: (e: TreeSelectionEvent) -> Unit) {
        tree.addTreeSelectionListener(listener)
    }

    private class Renderer : DefaultTreeCellRenderer() {
        override fun getTreeCellRendererComponent(
            tree: JTree?,
            value: Any?,
            selected: Boolean,
            expanded: Boolean,
            isLeaf: Boolean,
            rowIndex: Int,
            hasFocus: Boolean
        ): Component {
            if (isLeaf)
                try { leafIcon = AnyFile.getIcon(value.toString().toLowerCase()) }
                catch (e: Exception) {}
            return super.getTreeCellRendererComponent(tree, value, selected, expanded, isLeaf, rowIndex, hasFocus)
        }
    }

    private class DecentTreeUI : BasicTreeUI() {
        override fun paintVerticalLine(g: Graphics?, c: JComponent?, x: Int, top: Int, bottom: Int) {}
        override fun paintVerticalPartOfLeg(g: Graphics?, clipBounds: Rectangle?, insets: Insets?, path: TreePath?) {}
        override fun paintHorizontalPartOfLeg(g: Graphics?, clipBounds: Rectangle?, insets: Insets?, bounds: Rectangle?,
                                              path: TreePath?, row: Int, isExpanded: Boolean, hasBeenExpanded: Boolean, isLeaf: Boolean) {}
    }
}