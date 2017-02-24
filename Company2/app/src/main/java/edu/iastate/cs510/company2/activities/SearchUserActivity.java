package edu.iastate.cs510.company2.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import edu.iastate.cs510.company2.socialpolling.R;

public class SearchUserActivity extends AppCompatActivity {

    Button button;
    TextView textView;
    String mstring;
    int count = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        textView = (TextView) findViewById(R.id.disp);
        textView.setTextSize(25);
        Intent intent = getIntent();
        mstring = intent.getStringExtra("cat1");
        textView.setText(mstring);


        button = (Button) findViewById(R.id.follow);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                if(count%2==0)button.setText("Unfollow");
                if(count%2==1)button.setText("Follow");
            }
        });
    }
}
