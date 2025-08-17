package com.example.habitmaster.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Category;
import com.example.habitmaster.ui.activities.CategoriesActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private final List<Category> categories;

    public CategoryAdapter(Context context, List<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void addCategory(Category category) {
        categories.add(category);
        notifyItemInserted(categories.size() - 1);
    }

    public void updateCategory(Category updatedCategory) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(updatedCategory.getId())) {
                categories.set(i, updatedCategory);
                notifyItemChanged(i);
                break;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        View colorView;
        Button btnEdit;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            colorView = itemView.findViewById(R.id.viewColor);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }

        public void bind(Category category) {
            tvName.setText(category.getName());
            colorView.setBackgroundColor(category.getColor());

            btnEdit.setOnClickListener(v -> {
                ((CategoriesActivity) context).openCategoryDialog(category);
            });

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(context)   // use adapter's context
                        .setTitle("Delete Category")
                        .setMessage("Are you sure you want to delete the category \"" + category.getName() + "\"?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            // Call delete method from your activity
                            ((CategoriesActivity) context).deleteCategory(category.getId());
                            Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }
    }

    public void removeCategory(String categoryId) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getId().equals(categoryId)) {
                categories.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

}


