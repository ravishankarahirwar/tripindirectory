package directory.tripin.com.tripindirectory.newactivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import directory.tripin.com.tripindirectory.R;
import directory.tripin.com.tripindirectory.model.PartnerInfoPojo;

public class PartnerDetailScrollingActivity extends AppCompatActivity {


    SliderLayout sliderLayout;
    DocumentReference mUserDocRef;
    String uid;
    Toolbar toolbar;
    CollapsingToolbarLayout toolbarLayout;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_detail_scrolling);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle("subtitle");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(getIntent().getExtras().getString("cname")+"");
        uid = getIntent().getExtras().getString("uid");
        mUserDocRef = FirebaseFirestore.getInstance()
                .collection("partners").document(uid);
        init();

        setListners();

        FloatingActionButton fab = findViewById(R.id.fabCall);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setListners() {

        mUserDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                //get all partner data
                if(documentSnapshot.exists()){
                    PartnerInfoPojo partnerInfoPojo = documentSnapshot.toObject(PartnerInfoPojo.class);
                    toolbarLayout.setTitle(partnerInfoPojo.getmCompanyName());


                    if(partnerInfoPojo.getmImagesUrl()!=null){

                        Picasso.with(PartnerDetailScrollingActivity.this)
                                .load("https://source.unsplash.com/random")
                        .resize(10, 10)
                                .into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                        Palette.from(bitmap)
                                                .generate(new Palette.PaletteAsyncListener() {
                                                    @Override
                                                    public void onGenerated(Palette palette) {
                                                        Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                                        if (textSwatch == null) {
                                                            Toast.makeText(PartnerDetailScrollingActivity.this, "Null swatch :(", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        backgroundGroup.setBackgroundColor(textSwatch.getRgb());
                                                        titleColorText.setTextColor(textSwatch.getTitleTextColor());
                                                        bodyColorText.setTextColor(textSwatch.getBodyTextColor());
                                                    }
                                                });
                                    }

                                    @Override
                                    public void onBitmapFailed(Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                        for(String url: partnerInfoPojo.getmImagesUrl()){
                            DefaultSliderView defaultSliderView =new DefaultSliderView(PartnerDetailScrollingActivity.this);
                            defaultSliderView.image(url).setScaleType(BaseSliderView.ScaleType.CenterCrop);
                            sliderLayout.addSlider(defaultSliderView);
                        }
                    }else {
                        DefaultSliderView defaultSliderView =new DefaultSliderView(PartnerDetailScrollingActivity.this);
                        defaultSliderView.image(R.drawable.company_logo).setScaleType(BaseSliderView.ScaleType.Fit);
                        sliderLayout.addSlider(defaultSliderView);
                    }
                }else {

                }


            }
        });
    }

    private void init() {



        sliderLayout = findViewById(R.id.slider);
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(9000);
//        sliderLayout.setCurrentPosition(0);


    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        sliderLayout.stopAutoCycle();
        super.onStop();
    }
}
