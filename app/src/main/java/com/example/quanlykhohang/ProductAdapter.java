package com.example.quanlykhohang;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends ArrayAdapter<Product> {

    private final Context context;
    private final List<Product> products;
    private final NumberFormat currencyFormat;

    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
        this.currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_product_name);
            holder.tvPrice = convertView.findViewById(R.id.tv_product_price);
            holder.tvCategory = convertView.findViewById(R.id.tv_product_category);
            holder.tvQuantity = convertView.findViewById(R.id.tv_product_quantity);
            holder.tvLowStock = convertView.findViewById(R.id.tv_low_stock_badge);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(currencyFormat.format(product.getPrice()) + " đ");
        holder.tvCategory.setText(product.getCategory());
        holder.tvQuantity.setText("Tồn: " + product.getQuantity());

        if (product.isLowStock()) {
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.danger));
            holder.tvLowStock.setVisibility(View.VISIBLE);
            convertView.setBackgroundResource(R.drawable.bg_item_low_stock);
        } else {
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            holder.tvLowStock.setVisibility(View.GONE);
            convertView.setBackgroundResource(R.drawable.bg_item_normal);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvPrice;
        TextView tvCategory;
        TextView tvQuantity;
        TextView tvLowStock;
    }
}
