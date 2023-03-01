package com.example.babybuy.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.babybuy.Adapter.ProductListAdapter;
import com.example.babybuy.Database.Database;
import com.example.babybuy.Model.ProductDataModel;
import com.example.babybuy.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity {

    TextView productaddcamera, productaddgallery;
    Button productaddbtn;
    ImageView productaddimage, backicon;
    EditText productaddname, productadddes, productaddquantity, productaddprice;
    final int CAMERA_CODE = 100;
    final int GALLERY_CODE = 200;
    ProductDataModel productDataModel;
    int id = 0;
    int procatidlist = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        initialview();
        editview();

    }

    private void editview() {
        if (getIntent().getBundleExtra("itemData") != null) {
            //Database connection
            Database db = new Database(this);

            Bundle bundle = getIntent().getBundleExtra("itemData");
            id = bundle.getInt("id");
            productaddbtn.setText("Update Product");
            productaddname.setText(bundle.getString("name"));
            productaddquantity.setText(String.valueOf(bundle.getInt("quantity")));
            productaddprice.setText(String.valueOf(bundle.getDouble("price")));
            productadddes.setText(bundle.getString("description"));
            int prdStatus = bundle.getInt("prdStatus");
            //set image from pass another class that wants to be update
            byte[] bytes = bundle.getByteArray("image");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            productaddimage.setImageBitmap(bitmap);
            productaddgallery = findViewById(R.id.productaddfromgallery);
            productaddcamera = findViewById(R.id.productaddfromcamera);
            backicon = findViewById(R.id.backimgf);

            //category id value from Intent data pass
            procatidlist = getIntent().getExtras().getInt("pcid");
//            int procatidlist = getIntent().getExtras().getInt("pcid");
//            int procatidlist = bundle.getInt("pcid");

            //add product image from gallery
            productaddgallery.setOnClickListener(v -> {
                Intent igallery = new Intent(Intent.ACTION_PICK);
                igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(igallery, GALLERY_CODE);
            });

            //add product image from camera
            productaddcamera.setOnClickListener(v -> {
                Intent icamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(icamera, CAMERA_CODE);
            });

            //add product data to product table of database
            productaddbtn.setOnClickListener(v -> {

                productDataModel = new ProductDataModel();
                productDataModel.setProductid(id);
                productDataModel.setProductimage(convertImageViewToByteArray(productaddimage));
                productDataModel.setProductname(productaddname.getText().toString());
                productDataModel.setProductquantity(Integer.parseInt(productaddquantity.getText().toString()));
                productDataModel.setProductprice(Double.parseDouble(productaddprice.getText().toString()));
                productDataModel.setProductdescription(productadddes.getText().toString());
                productDataModel.setProductstatus(prdStatus);
                productDataModel.setProductcategoryid(procatidlist);

                long result = db.updateProduct(productDataModel, id);
                if (result == -1) {
                    Toast.makeText(this, "Product Update Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Product Updated Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProductActivity.this, ProductListActivity.class);
                    intent.putExtra("idd", procatidlist);
                    startActivity(intent);
                }
            });

            //back to productlistactivity
            backicon.setOnClickListener(v -> {
                ProductListActivity activity = new ProductListActivity();
                activity.priceresult();
                Intent intent = new Intent(AddProductActivity.this, activity.getClass());
                intent.putExtra("idd", procatidlist);
                startActivity(intent);
            });
        }

    }

    private void initialview() {
        //Database connection
        Database db = new Database(this);

        productaddbtn = findViewById(R.id.productaddbtnid);
        productaddname = findViewById(R.id.productaddtitleid);
        productaddquantity = findViewById(R.id.productaddquantityid);
        productaddprice = findViewById(R.id.productaddpriceid);
        productadddes = findViewById(R.id.productadddesid);
        productaddimage = findViewById(R.id.productaddimageid);
        productaddgallery = findViewById(R.id.productaddfromgallery);
        productaddcamera = findViewById(R.id.productaddfromcamera);
        backicon = findViewById(R.id.backimgf);

        //category id value from Intent data pass
        procatidlist = getIntent().getExtras().getInt("pcid");


        //add product image from gallery
        productaddgallery.setOnClickListener(v -> {
            Intent igallery = new Intent(Intent.ACTION_PICK);
            igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(igallery, GALLERY_CODE);
        });

        //add product image from camera
        productaddcamera.setOnClickListener(v -> {
            Intent icamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(icamera, CAMERA_CODE);
        });

        //add product data to product table of database
        productaddbtn.setOnClickListener(v -> {

            Integer prdstatus = -1;
            productDataModel = new ProductDataModel();
            productDataModel.setProductimage(convertImageViewToByteArray(productaddimage));
            productDataModel.setProductname(productaddname.getText().toString());
            productDataModel.setProductquantity(Integer.parseInt(productaddquantity.getText().toString()));
            productDataModel.setProductprice(Double.parseDouble(productaddprice.getText().toString()));
            productDataModel.setProductdescription(productadddes.getText().toString());
            productDataModel.setProductstatus(prdstatus);
            productDataModel.setProductcategoryid(procatidlist);

            long result = db.productadd(productDataModel);
            if (result == -1) {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Succcess", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddProductActivity.this, ProductListActivity.class);
                intent.putExtra("idd", procatidlist);
                startActivity(intent);
            }
        });

        //back to productlistactivity
        backicon.setOnClickListener(v -> {
            Intent intent = new Intent(AddProductActivity.this, ProductListActivity.class);
            intent.putExtra("idd", procatidlist);
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_CODE) {
                //for camera
                Bitmap img = (Bitmap) (data.getExtras().get("data"));
                productaddimage.setImageBitmap(img);
            } else if (requestCode == GALLERY_CODE) {
                //for gallery
                productaddimage.setImageURI(data.getData());
            }
        }
    }

    private byte[] convertImageViewToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}