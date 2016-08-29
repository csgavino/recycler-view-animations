package com.example.sandbox2


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import java.util.*

interface ColorsController {
    fun onClick(position: Int, checkedRadioId: Int)
}

class ColorsControllerImpl(val adapter: ColorsAdapter) : ColorsController {
    override fun onClick(position: Int, checkedRadioId: Int) {
        when (checkedRadioId) {
            R.id.add_btn -> adapter.addItemAt(position + 1, randomColor())
            R.id.change_btn -> adapter.changeItemAt(position, randomColor())
            R.id.delete_btn -> adapter.deleteItemAt(position)
        }
    }
}

class ColorsFragment() : Fragment(), ColorsCallback {

    val colorsAdapter = ColorsAdapter()

    lateinit var recyclerView: RecyclerView
    lateinit var radioGroup: RadioGroup

    lateinit var colorsController: ColorsController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_colors, container, false)

        radioGroup = view.findViewById(R.id.radioGroup) as RadioGroup

        recyclerView = view.findViewById(R.id.colors) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = colorsAdapter

        recyclerView.layoutManager = MyLayoutManager(view.context)
//        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.itemAnimator = MyItemAnimator()

        colorsAdapter.callback = this
        colorsController = ColorsControllerImpl(colorsAdapter)

        return view
    }

    override fun onClick(itemView: View) {
        val position = recyclerView.getChildAdapterPosition(itemView)
        if (position == RecyclerView.NO_POSITION) return

        colorsController.onClick(position,
                radioGroup.checkedRadioButtonId)
    }

}

class MyLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun supportsPredictiveItemAnimations(): Boolean {
        return true
    }
}

class MyItemAnimator() : DefaultItemAnimator() {

    private var animatorCache: HashMap<ColorsViewHolder, AnimatorInfo> = HashMap()

    private class ColorsHolderInfo(vh: ColorsViewHolder) : ItemHolderInfo() {
        var color: Int = (vh.itemView.background as ColorDrawable).color
        var text: String = vh.colorTextView.text.toString()
    }

    private data class AnimatorInfo(
            val overallAnim: AnimatorSet,
            val fadeToBlackAnim: ObjectAnimator,
            val fadeFromBlackAnim: ObjectAnimator,
            val oldTextRotate: ObjectAnimator,
            val newTextRotate: ObjectAnimator
    )

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun recordPreLayoutInformation(state: RecyclerView.State,
                                            viewHolder: RecyclerView.ViewHolder,
                                            changeFlags: Int,
                                            payloads: MutableList<Any>): ItemHolderInfo {
        return ColorsHolderInfo(viewHolder as ColorsViewHolder)
    }

    override fun recordPostLayoutInformation(state: RecyclerView.State,
                                             viewHolder: RecyclerView.ViewHolder): ItemHolderInfo {
        return ColorsHolderInfo(viewHolder as ColorsViewHolder)
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        return super.animateAdd(holder)
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder,
                               newHolder: RecyclerView.ViewHolder,
                               itemPreInfo: ItemHolderInfo,
                               itemPostInfo: ItemHolderInfo): Boolean {

        val vh = newHolder as ColorsViewHolder
        val preInfo = itemPreInfo as ColorsHolderInfo
        val postInfo = itemPostInfo as ColorsHolderInfo

        val fadeToBlack = ObjectAnimator.ofArgb(vh.itemView,
                "backgroundColor",
                preInfo.color,
                Color.BLACK)

        val fadeFromBlack = ObjectAnimator.ofArgb(vh.itemView,
                "backgroundColor",
                Color.BLACK,
                postInfo.color)

        val bgAnim = AnimatorSet()
        bgAnim.playSequentially(fadeToBlack, fadeFromBlack)

        val oldTextRotate = ObjectAnimator.ofFloat(vh.colorTextView,
                View.ROTATION_X,
                0f,
                90f)

        val newTextRotate = ObjectAnimator.ofFloat(vh.colorTextView,
                View.ROTATION_X,
                -90f,
                0f)

        val textAnim = AnimatorSet()
        textAnim.playSequentially(oldTextRotate, newTextRotate)

        oldTextRotate.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                vh.colorTextView.text = preInfo.text
            }

            override fun onAnimationEnd(animation: Animator?) {
                vh.colorTextView.text = postInfo.text
            }
        })

        val overallAnim = AnimatorSet()
        overallAnim.playTogether(bgAnim, textAnim)
        overallAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                dispatchAnimationFinished(newHolder)
                animatorCache.remove(newHolder)
            }
        })

        val runningInfo = animatorCache[newHolder]
        if (runningInfo != null) {
            val firstHalf = runningInfo.oldTextRotate.isRunning
            if (firstHalf) {
                oldTextRotate.currentPlayTime = runningInfo.oldTextRotate.currentPlayTime
                fadeToBlack.currentPlayTime = runningInfo.fadeToBlackAnim.currentPlayTime
            } else {
                newTextRotate.currentPlayTime = runningInfo.newTextRotate.currentPlayTime
                fadeFromBlack.currentPlayTime = runningInfo.fadeFromBlackAnim.currentPlayTime
            }

            runningInfo.overallAnim.cancel()
        }

        animatorCache[newHolder] = AnimatorInfo(
                overallAnim,
                fadeToBlack,
                fadeFromBlack,
                oldTextRotate,
                newTextRotate)

        overallAnim.start()

        return super.animateChange(oldHolder, newHolder, itemPreInfo, itemPostInfo)
    }
}
