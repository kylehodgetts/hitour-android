package uk.ac.kcl.stranders.hitour;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class FeedActivity extends AppCompatActivity {

    private RecyclerView mFeed;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFeed = (RecyclerView) findViewById(R.id.rv_feed);

        mFeed.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        mFeed.setLayoutManager(mLayoutManager);
        FeedAdapter adapter = new FeedAdapter(PrototypeData.getCursor(), this);
        mFeed.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });
    }

    private void scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(ScanningActivity.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.d("ScanningActivity", "Scan Cancelled");
                Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.d("ScanningActivity", "Scan Successful");
                Log.d("ScanningActivity", "Barcode Found: " + result.getContents());
                Toast.makeText(this, result.getContents(), Toast.LENGTH_SHORT).show();

                // TODO: Needs to be changed when DB ready to search QR code data with DB and display relevant DetailActivity Page
                if(result.getContents().matches("\\d+")  && PrototypeData.containsId(Integer.parseInt(result.getContents()))) {
                    Intent intent = new Intent(this, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_BUNDLE, Integer.parseInt(result.getContents()));
                    startActivity(intent);
                }
                else {
                    Log.d("FeedActivity", "Point for " + result.getContents() + " not found!");
                    Toast.makeText(this, "Point Not Found, Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
