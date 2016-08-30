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
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioGroup
import java.util.*

interface ColorsView {
    fun onLayoutManagerSelected(layoutManager: RecyclerView.LayoutManager)

    fun onItemAnimatorSelected(itemAnimator: SimpleItemAnimator)

    fun addItemAt(position: Int, color: Long)

    fun changeItemAt(position: Int, color: Long)

    fun deleteItemAt(position: Int)
}

interface ColorsController {
    fun onClick(position: Int, checkedRadioId: Int)

    fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean)
}

interface ColorsGenerator {
    fun generate(): Long
}

class ColorsGeneratorImpl : ColorsGenerator {
    override fun generate(): Long = COLORS.random()

}

class ColorsControllerImpl(
        val view: ColorsView,
        val colorsGenerator: ColorsGenerator = ColorsGeneratorImpl()
) : ColorsController {

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) = when (buttonView.id) {
        R.id.predictive_animations ->
            view.onLayoutManagerSelected(
                    if (isChecked) MyLayoutManager(buttonView.context)
                    else LinearLayoutManager(buttonView.context))

        R.id.custom_animator ->
            view.onItemAnimatorSelected(
                    if (isChecked) MyItemAnimator()
                    else DefaultItemAnimator())

        else -> throw IllegalArgumentException()
    }

    override fun onClick(position: Int, checkedRadioId: Int) = when (checkedRadioId) {
        R.id.add_btn -> view.addItemAt(position + 1, colorsGenerator.generate())
        R.id.change_btn -> view.changeItemAt(position, colorsGenerator.generate())
        R.id.delete_btn -> view.deleteItemAt(position)

        else -> throw IllegalArgumentException()
    }
}

class ColorsFragment() : Fragment(),
        ColorsView,
        ColorsCallback,
        CompoundButton.OnCheckedChangeListener {

    val colorsAdapter = ColorsAdapter()

    lateinit var recyclerView: RecyclerView
    lateinit var radioGroup: RadioGroup
    lateinit var colorsController: ColorsController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_colors, container, false)

        radioGroup = view.findViewById(R.id.radio_group) as RadioGroup

        val predictiveAnimations = view.findViewById(R.id.predictive_animations) as CheckBox
        predictiveAnimations.setOnCheckedChangeListener(this)

        val customAnimator = view.findViewById(R.id.custom_animator) as CheckBox
        customAnimator.setOnCheckedChangeListener(this)

        recyclerView = view.findViewById(R.id.colors) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.adapter = colorsAdapter

        colorsAdapter.callback = this
        colorsController = ColorsControllerImpl(this)

        return view
    }

    override fun onClick(itemView: View) {
        val position = recyclerView.getChildAdapterPosition(itemView)
        if (position == RecyclerView.NO_POSITION) return

        colorsController.onClick(position,
                radioGroup.checkedRadioButtonId)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) =
            colorsController.onCheckedChanged(buttonView, isChecked)

    override fun onLayoutManagerSelected(layoutManager: RecyclerView.LayoutManager) {
        recyclerView.layoutManager = layoutManager
    }

    override fun onItemAnimatorSelected(itemAnimator: SimpleItemAnimator) {
        recyclerView.itemAnimator = itemAnimator
    }

    override fun addItemAt(position: Int, color: Long) =
            colorsAdapter.addItemAt(position, color)

    override fun changeItemAt(position: Int, color: Long) =
            colorsAdapter.changeItemAt(position, color)

    override fun deleteItemAt(position: Int) =
            colorsAdapter.deleteItemAt(position)

}

class MyLayoutManager(context: Context) : LinearLayoutManager(context) {
    override fun supportsPredictiveItemAnimations(): Boolean = true
}

class MyItemAnimator() : DefaultItemAnimator() {

    private var animatorCache: HashMap<ColorsViewHolder, AnimatorInfo> = HashMap()

    private class ColorsHolderInfo() : ItemHolderInfo() {
        var color: Int = -1
        lateinit var text: String

        fun setFrom(holder: ColorsViewHolder): ItemHolderInfo {
            color = (holder.itemView.background as ColorDrawable).color
            text = holder.colorTextView.text.toString()

            return super.setFrom(holder)
        }
    }

    private data class AnimatorInfo(
            val overallAnim: AnimatorSet,
            val fadeToBlackAnim: ObjectAnimator,
            val fadeFromBlackAnim: ObjectAnimator,
            val oldTextRotate: ObjectAnimator,
            val newTextRotate: ObjectAnimator
    )

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean = true

    override fun recordPreLayoutInformation(state: RecyclerView.State,
                                            viewHolder: RecyclerView.ViewHolder,
                                            changeFlags: Int,
                                            payloads: MutableList<Any>): ItemHolderInfo {
        return ColorsHolderInfo().setFrom(viewHolder as ColorsViewHolder)
    }

    override fun recordPostLayoutInformation(state: RecyclerView.State,
                                             viewHolder: RecyclerView.ViewHolder): ItemHolderInfo {
        return ColorsHolderInfo().setFrom(viewHolder as ColorsViewHolder)
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
