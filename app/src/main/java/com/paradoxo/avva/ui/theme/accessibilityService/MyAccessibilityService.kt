package com.paradoxo.avva.ui.theme.accessibilityService

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi

class MyAccessibilityService : AccessibilityService() {

    private lateinit var receiver: BroadcastReceiver


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onServiceConnected() {
        Log.d("NodeServices", "Accessibility service connected")

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == "com.paradoxo.avva.ACTION_IMAGE_CLICKED") {
                    Log.d("NodeServices", "BroadcastReceiver recebido")
                }
            }
        }


        val filter = IntentFilter("com.paradoxo.avva.ACTION_IMAGE_CLICKED")
        registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)

    }

    override fun onInterrupt() {}

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        Log.d("NodeServices", "entrou no onAccessibilityEvent")
        val source: AccessibilityNodeInfo? = event.source
        if (source == null) {
            Log.d("NodeServices", "source is null")
            return
        }
        Log.d("NodeServices", "source $source")

        getAllTextsFromScreen(rootInActiveWindow)
        getChildForTextsFromScreen(rootInActiveWindow)
        getListAllClickableNodes(rootInActiveWindow)

        val rowNode: AccessibilityNodeInfo? = getListItemNodeInfo(source)
        if (rowNode == null) {
            Log.d("NodeServices", "rowNode is null")
            return
        }
        Log.d("NodeServices", "rowNode $rowNode")

        // Using this parent, get references to both child nodes, the label, and the
        // checkbox.
        val taskLabel: CharSequence = rowNode.getChild(0)?.text ?: run {
            rowNode.recycle()
            return
        }

        Log.d("NodeServices", "taskLabel: $taskLabel")

        val isComplete: Boolean = rowNode.getChild(1)?.isChecked ?: run {
            rowNode.recycle()
            return
        }

        Log.d("NodeServices", "isComplete: $isComplete")

        // Determine what the task is and whether it's complete based on the text
        // inside the label, and the state of the checkbox.
        if (rowNode.childCount < 2 || !rowNode.getChild(1).isCheckable) {
            rowNode.recycle()
            return
        }

        val completeStr: String = if (isComplete) {
            "Checked"
        } else {
            "Not Checked"
        }
        val reportStr = "$taskLabel$completeStr"

        Log.d("NodeServices", "reportStr: $reportStr")
    }


    private fun getListItemNodeInfo(source: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        var current = source
        while (true) {
            val parent = current.parent ?: return null

            Log.d("NodeServices", "Parent: $parent")

            // Verifique se o nó atual é um item de lista. Isso pode variar dependendo da implementação da UI.
            // Você pode precisar verificar a classe do nó ou outras propriedades.
            if (parent.className == "android.widget.ListView" || parent.className == "androidx.recyclerview.widget.RecyclerView") {
                return current
            }
            // Não é um item de lista, continue navegando para cima na árvore.
            current = parent
        }
    }

    private fun getAllTextsFromScreen(nodeInfo: AccessibilityNodeInfo?): List<CharSequence> {
        val texts = mutableListOf<CharSequence>()

        fun collectTexts(node: AccessibilityNodeInfo?) {
            if (node == null) return

            Log.d("NodeServicesNode", "node: $node")
            if (node.text != null) {
                texts.add(node.text)
            }

            for (i in 0 until node.childCount) {
                collectTexts(node.getChild(i))
            }
        }


        collectTexts(nodeInfo)
        Log.d("NodeServicesText", "texts: $texts")
        return texts
    }

    private fun getListAllClickableNodes(nodeInfo: AccessibilityNodeInfo?): List<String> {
        val clickableNodes = mutableListOf<String>()

        fun collectClickableNodes(node: AccessibilityNodeInfo?) {
            if (node == null) return

            if (node.isClickable && node.text != null) {
                clickableNodes.add(node.text.toString())
            }

            for (i in 0 until node.childCount) {
                val internalNode = node.getChild(i)

                if (internalNode != null && internalNode.isClickable && internalNode.text != null) {
                    clickableNodes.add(internalNode.text.toString())

                    // se o texto for igual a "Teste1234456" então clicar no nó
                    if (internalNode.text.toString() == "Teste1234456") {
                        internalNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        return@collectClickableNodes
                    }
                }

                collectClickableNodes(internalNode)
            }
        }

        collectClickableNodes(nodeInfo)
        return clickableNodes
    }


    private fun getChildForTextsFromScreen(nodeInfo: AccessibilityNodeInfo?): List<CharSequence> {
        val texts = mutableListOf<CharSequence>()
        val allNodes = mutableListOf<AccessibilityNodeInfo>()

        fun collectTexts(node: AccessibilityNodeInfo?) {
            if (node == null) return

            Log.d("NodeServicesNode", "node: $node")
            if (node.text != null) {
                texts.add(node.text)
                allNodes.add(node)
            }

            for (i in 0 until node.childCount) {
                val internalNode = node.getChild(i)
                if (internalNode.text != null) {
                    allNodes.add(internalNode)
                    texts.add(internalNode.text)
                    collectTexts(internalNode)
                }
            }
        }


        collectTexts(nodeInfo)
        Log.d("NodeServicesText", "texts: $texts")
        allNodes.forEach { internalNode ->
            Log.d("internalNode", "----------------------")
            Log.d("internalNode", "node.text: ${internalNode.text}")
            Log.d("internalNode", "node.text coordes: ${internalNode.isHeading}")
            Log.d("internalNode", "parent: ${internalNode.parent.text}")
            Log.d("internalNode", "parent: ${internalNode.parent}")
        }
        return texts
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

}