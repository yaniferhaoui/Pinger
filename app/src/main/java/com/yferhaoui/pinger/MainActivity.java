package com.yferhaoui.pinger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<Server> servers = new ArrayList<Server>();
    private DBHelper dataBase;

    Button sendTo = null;
    private Map.Entry<Server, RelativeLayout> serverSelected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.toolbar));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.setButtonSendTo();
        this.setButtonAdd();
        this.dataBase = new DBHelper(this);
        this.loadServers();

        this.displayServers();
    }

    private final void setButtonSendTo() {
        this.sendTo = (Button)findViewById(R.id.send_to);
        this.sendTo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (MainActivity.this.serverSelected != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
                    intent.putExtra("sms_body", "The server " +
                            MainActivity.this.serverSelected.getKey().getName() +
                            " is " + MainActivity.this.serverSelected.getKey().getState() + " !");
                    startActivity(intent);
                    MainActivity.this.disableSend();
                } else {
                    Toast.makeText(MainActivity.this, "Select Server Before", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private final void setButtonAdd() {
        Button add = (Button)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MainActivity.this.launchAdd();
                MainActivity.this.serverSelected = null;
            }
        });
    }

    private final void loadServers() {
        this.servers = this.dataBase.getServers();
    }

    private final void displayServers() {
        final LinearLayout layout = (LinearLayout)findViewById(R.id.servers);
        layout.removeAllViews();

        for (final Server server : this.servers) {
            server.sendPingRequest();
            final View child = getLayoutInflater().inflate(R.layout.server, null);
            layout.addView(child);

            ((TextView)child.findViewById(R.id.name))
                    .setText(server.getName());

            TextView theState = (TextView)child.findViewById(R.id.state);
            theState.setText(server.getState());

            if (server.isOnline()) {
                theState.setTextColor(this.getResources().getColor(R.color.online));
            } else {
                theState.setTextColor(this.getResources().getColor(R.color.offline));
            }

            child.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View arg0) {


                    if (MainActivity.this.serverSelected != null) {

                        if (MainActivity.this.serverSelected.getValue() != arg0) {
                            MainActivity.this.disableSend();
                            MainActivity.this.serverSelected = new AbstractMap.SimpleEntry<Server, RelativeLayout>( server, (RelativeLayout)arg0);
                            MainActivity.this.enableSend();
                        } else {
                            MainActivity.this.disableSend();
                        }
                    } else {
                        MainActivity.this.serverSelected = new AbstractMap.SimpleEntry<Server, RelativeLayout>( server, (RelativeLayout)arg0);
                        MainActivity.this.enableSend();
                    } return true;
                }
            });

            Button remove = ((Button)child.findViewById(R.id.remove));
            remove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {

                    MainActivity.this.dataBase.deleteServer(server.getIp());
                    child.setVisibility(View.GONE);
                    if (MainActivity.this.serverSelected != null && MainActivity.this.serverSelected.getKey() == server) {
                        MainActivity.this.disableSend();
                    }
                    MainActivity.this.servers.remove(server);
                    layout.removeView(child);
                }
            });

            Button modify = ((Button)child.findViewById(R.id.modify));
            modify.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    MainActivity.this.launchUpdate(server);
                }
            });
        }

    }

    private final void launchAdd() {
        Intent intent = new Intent(this, InsertServer.class);
        intent.putExtra("action", "Add");
        startActivity(intent);
        this.disableSend();
    }

    private final void launchUpdate(Server server) {
        Intent intent = new Intent(MainActivity.this, InsertServer.class);
        intent.putExtra("action", "Update");
        intent.putExtra("name", server.getName());
        intent.putExtra("ip", server.getIp());
        intent.putExtra("ipMac", server.getIpMac());
        intent.putExtra("externalPort", server.getExternalPort());
        intent.putExtra("internalPort", server.getInternalPort());
        intent.putExtra("isPublic", server.isPublic());
        intent.putExtra("expiryDate", server.getExpiryDate());
        startActivity(intent);
        this.disableSend();
    }

    private final void enableSend() {
        this.serverSelected.getValue().setBackground(getResources().getDrawable(R.color.background_server));
        this.serverSelected.getValue().setBackground(getResources().getDrawable(R.color.background_server_selected));
        this.sendTo.setText("Send To");
        this.sendTo.setBackground(getResources().getDrawable(R.color.background_button));
    }

    private final void disableSend() {
        if (this.serverSelected != null) {
            this.serverSelected.getValue().setBackground(getResources().getDrawable(R.color.background_server));
            this.serverSelected = null;
            this.sendTo.setText("Select Server");
            this.sendTo.setTextColor(MainActivity.this.getResources().getColor(R.color.text_button_block));
            this.sendTo.setBackground(getResources().getDrawable(R.color.background_button_block));
        }
    }

    @Override
    public void onResume()  {
        super.onResume();
        this.displayServers();
    }

    @Override
    public void onRestart()  {
        super.onRestart();
        this.displayServers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } return super.onOptionsItemSelected(item);
    }
}
