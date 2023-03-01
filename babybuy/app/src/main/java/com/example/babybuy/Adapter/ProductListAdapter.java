package com.example.babybuy.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babybuy.Activity.AddProductActivity;
import com.example.babybuy.Activity.ProductListActivity;
import com.example.babybuy.Database.Database;
import com.example.babybuy.Model.ProductDataModel;
import com.example.babybuy.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductDataModel> arrayList;


    public ProductListAdapter(Context context, ArrayList<ProductDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView producttitle, productdes, productprice, productquantity;
        CheckBox productstatuscheckbox;
        ImageView pedit, pdelete, productimage;
        CardView productcardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            producttitle = itemView.findViewById(R.id.product_title_id);
            productdes = itemView.findViewById(R.id.product_des_id);
            productprice = itemView.findViewById(R.id.product_price_id);
            productquantity = itemView.findViewById(R.id.product_quantity_id);
            productstatuscheckbox = itemView.findViewById(R.id.productpurchased);
            productimage = itemView.findViewById(R.id.product_img_id);
            pedit = itemView.findViewById(R.id.productlistedit);
            pdelete = itemView.findViewById(R.id.productlistdelete);
            productcardview = itemView.findViewById(R.id.cardview_id);


        }
    }

    @NonNull
    @Override
    public ProductListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.design_product_recyclerview, parent, false);
        ProductListAdapter.ViewHolder viewHolder = new ProductListAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDataModel pdm = arrayList.get(position);

        holder.producttitle.setText(pdm.getProductname());
        holder.productdes.setText(pdm.getProductdescription());
        holder.productprice.setText(String.valueOf(pdm.getProductprice()));
        holder.productquantity.setText(String.valueOf(pdm.getProductquantity()));
        Bitmap ImageDataInBitmap = BitmapFactory.decodeByteArray(pdm.getProductimage(), 0, pdm.getProductimage().length);
        holder.productimage.setImageBitmap(ImageDataInBitmap);

        if (pdm.getProductstatus() > 0) {
            holder.productstatuscheckbox.setChecked(true);
        }

        holder.productstatuscheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                int position = holder.getAdapterPosition();
                if (holder.productstatuscheckbox.isChecked()) {
                    int productstsvalue = 1;
                    Database db = new Database(context);
                    db.productpurchased(productstsvalue, pdm.getProductid());
                    if (context instanceof ProductListActivity) {
                        ProductListActivity activity = (ProductListActivity) context;
                        activity.priceresult();
                    }


                } else {
                    int productstsvalue = -1;
                    Database db = new Database(context);
                    db.productpurchased(productstsvalue, pdm.getProductid());
                    if (context instanceof ProductListActivity) {
                        ProductListActivity activity = (ProductListActivity) context;
                        activity.priceresult();
                    }
                }
            }
        });

        holder.pedit.setOnClickListener(x -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id", pdm.getProductid());
            bundle.putString("name", pdm.getProductname());
            bundle.putByteArray("image", pdm.getProductimage());
            bundle.putDouble("price", pdm.getProductprice());
            bundle.putString("description", pdm.getProductdescription());
            bundle.putInt("quantity", pdm.getProductquantity());
            Intent intent = new Intent(this.context, AddProductActivity.class);
            intent.putExtra("itemData", bundle);
            intent.putExtra("pcid", pdm.getProductcategoryid());

            context.startActivity(intent);

//            ((Activity) context).finish();
        });

        holder.pdelete.setOnClickListener(x -> {
            Database db = new Database(context);
            db.deleteproduct(pdm.getProductid());
            arrayList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
            if (context instanceof ProductListActivity) {
                ProductListActivity activity = (ProductListActivity) context;
                activity.priceresult();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}

