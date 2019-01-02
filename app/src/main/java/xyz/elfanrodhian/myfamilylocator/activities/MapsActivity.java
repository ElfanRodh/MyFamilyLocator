package xyz.elfanrodhian.myfamilylocator.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import xyz.elfanrodhian.myfamilylocator.R;
import xyz.elfanrodhian.myfamilylocator.models.FamilyMember;
import xyz.elfanrodhian.myfamilylocator.models.LocationModel;
import xyz.elfanrodhian.myfamilylocator.utils.Constants;
import xyz.elfanrodhian.myfamilylocator.utils.FirebaseUtils;

import static xyz.elfanrodhian.myfamilylocator.R.id.fab;
import static xyz.elfanrodhian.myfamilylocator.R.id.family_location_recycler_view;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;

    private Toolbar toolbar;
    private AppBarLayout appBarLayout;
    private boolean isToolbarHidden;

    private DatabaseReference reference;
    private String familyMemberUid;
    private FamilyMember familyMember;
    private BitmapDescriptor marker;

    private int zoomLevel;
    private CameraUpdate camera;
    private LatLng location;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        isToolbarHidden = false;
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        familyMemberUid = getIntent().getStringExtra(Constants.FAMILY_MEMBER_UID_STRING_EXTRA);
        reference = FirebaseUtils.getFamilyRef();
        getCurrentFamilyMember(familyMemberUid);
        marker = getBitmapDescriptor(R.drawable.marker);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        zoomLevel = 18;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View v = getLayoutInflater().inflate(R.layout.custom_marker_info_window, null);
                TextView name = (TextView) v.findViewById(R.id.info_window_name);
                if (familyMember != null) {
                    String memberName = familyMember.getFirstName() + "'s Location";
                    name.setText(memberName);

                    //Inisialisasi Google Play Services
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            buildGoogleApiClient();
                            mMap.setMyLocationEnabled(true);
                        }
                    }
                    else {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                }
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!isToolbarHidden) {
                    ObjectAnimator ABLTranslationOut = ObjectAnimator.ofFloat(appBarLayout, "translationY", 0f, -toolbar.getBottom());

                    AnimatorSet outAnimatorSet = new AnimatorSet();
                    outAnimatorSet.playTogether(ABLTranslationOut);
                    outAnimatorSet.setInterpolator(new DecelerateInterpolator());
                    outAnimatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    outAnimatorSet.start();
                    isToolbarHidden = true;
                } else {
                    ObjectAnimator ABLTranslationIn = ObjectAnimator.ofFloat(appBarLayout, "translationY", -toolbar.getBottom(), 0f);
                    ObjectAnimator FABAlphaIn = ObjectAnimator.ofFloat(fab, "alpha", 0.0f, 1.0f);

                    AnimatorSet inAnimatorSet = new AnimatorSet();
                    inAnimatorSet.playTogether(ABLTranslationIn, FABAlphaIn);
                    inAnimatorSet.setInterpolator(new DecelerateInterpolator());
                    inAnimatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {
                        }
                    });
                    inAnimatorSet.start();
                    isToolbarHidden = false;
                }
            }
        });

        reference.child(familyMemberUid).child(Constants.LOCATIONS_NODE_KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LocationModel locationModel = dataSnapshot.getValue(LocationModel.class);

                if (locationModel != null) {
                    Log.e(TAG, "Lat: " + locationModel.getLat());
                    Log.e(TAG, "Lon: " + locationModel.getLon());

                    mMap.clear();

                    location = new LatLng(locationModel.getLat(), locationModel.getLon());
                    mMap.addMarker(new MarkerOptions().position(location).icon(marker)).showInfoWindow();
                    // TODO: 26-Apr-17 implement zoom level control.
                    camera = CameraUpdateFactory.newLatLngZoom(location, zoomLevel);
                    mMap.animateCamera(camera);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this, "Database Error: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getCurrentFamilyMember(String uid) {
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                familyMember = dataSnapshot.getValue(FamilyMember.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        Context context = MapsActivity.this;
        Bitmap marker = getBitmapFromVectorDrawable(context, id);
        return BitmapDescriptorFactory.fromBitmap(marker);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        // Memberikan marker pada current location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //memindahkan camera map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        //stop update lokasi
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
}
