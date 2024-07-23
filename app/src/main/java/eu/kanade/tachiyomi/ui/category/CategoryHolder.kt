package eu.mkonic.tachiyomi.ui.category

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import eu.mkonic.tachiyomi.R
import eu.mkonic.tachiyomi.data.database.models.Category
import eu.mkonic.tachiyomi.databinding.CategoriesItemBinding
import eu.mkonic.tachiyomi.ui.base.holder.BaseFlexibleViewHolder
import eu.mkonic.tachiyomi.ui.category.CategoryPresenter.Companion.CREATE_CATEGORY_ORDER
import eu.mkonic.tachiyomi.util.system.getResourceColor
import java.util.Locale

/**
 * Holder used to display category items.
 *
 * @param view The view used by category items.
 * @param adapter The adapter containing this holder.
 */
class CategoryHolder(view: View, val adapter: CategoryAdapter) : BaseFlexibleViewHolder(view, adapter) {

    private val binding = CategoriesItemBinding.bind(view)
    init {
        binding.editButton.setOnClickListener {
            submitChanges()
        }
    }

    var createCategory = false
    private var regularDrawable: Drawable? = null

    /**
     * Binds this holder with the given category.
     *
     * @param category The category to bind.
     */
    fun bind(category: Category) {
        // Set capitalized title.
        binding.title.text = category.name.replaceFirstChar { it.titlecase(Locale.getDefault()) }
        binding.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submitChanges()
            }
            true
        }
        createCategory = category.order == CREATE_CATEGORY_ORDER
        if (createCategory) {
            binding.title.setTextColor(ContextCompat.getColor(itemView.context, R.color.material_on_background_disabled))
            regularDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_add_24dp)
            binding.image.isVisible = false
            binding.editButton.setImageDrawable(null)
            binding.editText.setText("")
            binding.editText.hint = binding.title.text
        } else {
            binding.title.setTextColor(itemView.context.getResourceColor(R.attr.colorOnBackground))
            regularDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.ic_drag_handle_24dp)
            binding.image.isVisible = true
            binding.editText.setText(binding.title.text)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun isEditing(editing: Boolean) {
        itemView.isActivated = editing
        binding.title.visibility = if (editing) View.INVISIBLE else View.VISIBLE
        binding.editText.visibility = if (!editing) View.INVISIBLE else View.VISIBLE
        if (editing) {
            binding.editText.requestFocus()
            binding.editText.selectAll()
            binding.editButton.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_check_24dp))
            binding.editButton.drawable.mutate().setTint(itemView.context.getResourceColor(R.attr.colorSecondary))
            showKeyboard()
            if (!createCategory) {
                binding.reorder.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete_24dp),
                )
                binding.reorder.setOnClickListener {
                    adapter.categoryItemListener.onItemDelete(flexibleAdapterPosition)
                    hideKeyboard()
                }
            }
        } else {
            if (!createCategory) {
                setDragHandleView(binding.reorder)
                binding.editButton.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_edit_24dp))
            } else {
                binding.editButton.setImageDrawable(null)
                binding.reorder.setOnTouchListener { _, _ -> true }
            }
            binding.editText.clearFocus()
            binding.editButton.drawable?.mutate()?.setTint(
                ContextCompat.getColor(itemView.context, R.color.gray_button),
            )
            binding.reorder.setImageDrawable(regularDrawable)
        }
    }

    private fun submitChanges() {
        if (binding.editText.isVisible) {
            if (adapter.categoryItemListener.onCategoryRename(
                    flexibleAdapterPosition,
                    binding.editText.text.toString(),
                )
            ) {
                isEditing(false)
                if (!createCategory) {
                    binding.title.text = binding.editText.text.toString()
                }
            }
        } else {
            itemView.performClick()
        }
        hideKeyboard()
    }

    private fun showKeyboard() {
        val inputMethodManager = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.editText, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun hideKeyboard() {
        val inputMethodManager = itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.editText.windowToken, 0)
    }

    override fun onActionStateChanged(position: Int, actionState: Int) {
        super.onActionStateChanged(position, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            binding.root.isDragged = true
        }
    }

    override fun onItemReleased(position: Int) {
        super.onItemReleased(position)
        adapter.categoryItemListener.onItemReleased(position)
        binding.root.isDragged = false
    }
}
