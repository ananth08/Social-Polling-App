package edu.iastate.cs510.company2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.iastate.cs510.company2.gateway.PsGateway;
import edu.iastate.cs510.company2.gateway.ReadMsg;
import edu.iastate.cs510.company2.gateway.Record;
import edu.iastate.cs510.company2.gateway.UpdateMsg;
import edu.iastate.cs510.company2.infrastructure.ServiceRegistry;
import edu.iastate.cs510.company2.models.IPoll;
import edu.iastate.cs510.company2.models.Poll;
import edu.iastate.cs510.company2.persistence.MemStoreGw;
import edu.iastate.cs510.company2.socialpolling.R;
/**
 * Created by swf94 on 12/2/2016.
 */
public class NewVotingPollActivity extends AppCompatActivity {
    private ListView myListView;
    private String username;
    public ServiceRegistry locator;
    public int p;
    int counter = -1;
    private RadioGroup choice;
    private Button sub;
    final List<Record> pollRecords = new ArrayList<>();
    ArrayList<String> keysList = new ArrayList<String>();
    Poll pollObj;
    Gson gson=new Gson();
    PsGateway gw;
    int c;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locator  = ServiceRegistry.getInstance();
        if(locator.getService(PsGateway.class) == null) {
            locator.register(PsGateway.class, new MemStoreGw());
        }
        setContentView(R.layout.activity_new_voting_poll);
        Bundle b = getIntent().getExtras();
        c=b.getInt("Position");
        context = NewVotingPollActivity.this;

        choice = (RadioGroup) findViewById(R.id.choices);
        sub = (Button) findViewById(R.id.button2);
        sub.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                for (int i = 0; i < choice.getChildCount(); i++) {
                    RadioButton r = (RadioButton) choice.getChildAt(i);
                    if (r.isChecked()) {
                        SharedPreferences myChoice = getSharedPreferences("mychoice", MODE_PRIVATE);
                        SharedPreferences.Editor editor = myChoice.edit();
                        editor.putString("pollID", Integer.toString(R.id.yourQuestion));
                        editor.putString("choiceID", Integer.toString(r.getId()));
                        editor.commit();

                        ArrayList<IPoll.Choice> choices = (ArrayList<IPoll.Choice>)pollObj.getChoices();
                        int value = choices.get(i).votes;
                        value = value + 1;
                        choices.get(i).votes = value;

                        UpdateMsg updatemessage = new UpdateMsg("http://iastate.510.com/SocialPolling", "Polls","key",c+"",pollObj);
                        gw.send(updatemessage);
                        Intent intent = new Intent(context,MainActivity.class);
                        context.startActivity(intent);

                        //String temp = Integer.toString(R.id.yourQuestion) + "  " + Integer.toString(r.getId());
                        //Toast.makeText(NewVotingPollActivity.this, r.getText(), Toast.LENGTH_SHORT).show();
                        //break;
                    }
                }
            }
        });
        //Create a text space to put page name here
        TextView textView = new TextView(this);
        textView.setTextSize(20);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_poll_history);
        //textView.setText("My Poll History");
        //Add page name to layout
        //layout.addView(textView);

        Context context = NewVotingPollActivity.this;
        myListView = (ListView)findViewById(R.id.listview);


        //get username from Shared preferences
        SharedPreferences mPrefs = getSharedPreferences("account", MODE_PRIVATE);
        username = mPrefs.getString("user", "User not found.");

        try {
            ArrayList<String> radioStr = new ArrayList<String>();

            ArrayList<String> pollsMessage = new ArrayList<String>();
            ArrayList<String> jSonList = new ArrayList<String>();
            ArrayList<Poll> pollclassList = new ArrayList<Poll>();

           // final List<Record> pollRecords = new ArrayList<>();
            ReadMsg readMessage = new ReadMsg("http://iastate.510.com/SocialPolling", "Polls", "key");

            gw = locator.getService(PsGateway.class);
            PsGateway.Response response = gw.send(readMessage);
            if(response.getStatus() != PsGateway.Status.success){
                //FAILED TO SEND MESSAGE
            }
            PsGateway.CbResponse fullReply = null;
            if (response instanceof PsGateway.CbResponse){
                fullReply = (PsGateway.CbResponse) response;
                pollRecords.addAll(fullReply.getPayload());
            }
            c=pollRecords.size()-c-1;
            JsonReader reader = new JsonReader(new StringReader(pollRecords.get(c).pLoad));
            reader.setLenient(true);
            pollObj=gson.fromJson(reader,Poll.class);

            TextView output = (TextView) findViewById(R.id.textView2);
            String line1 = pollObj.getQuestion().toString() + "\n";
            output.setText(line1);
            for (IPoll.Choice choice : pollObj.getChoices()){
                line1 += Integer.toString(choice.votes) + " votes  " + choice.choice.toString() + "\n";
                line1 += pollObj.getCategory() + "\n";
                line1 += "Created at " + pollObj.getCreated() + "\n";
                radioStr.add(choice.choice.toString());}
            pollsMessage.add(line1);
            pollclassList.add(pollObj);


            for (int i = 0; i < radioStr.size(); i++) {
                output = (TextView) this.findViewById(getResources().getIdentifier("radioButton" + (i + 5), "id", getPackageName()));
                output.setText(radioStr.get(i));
            }

        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
