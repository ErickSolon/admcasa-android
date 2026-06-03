package com.rede.admcasa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    public static String getMeuIp() {
        try {
            for (NetworkInterface netInterface :
                    Collections.list(NetworkInterface.getNetworkInterfaces())) {

                for (InetAddress endereco :
                        Collections.list(netInterface.getInetAddresses())) {

                    if (!endereco.isLoopbackAddress()
                            && endereco instanceof Inet4Address) {

                        return endereco.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<String> getIpsRede() {
        List<String> todosIps = new ArrayList<String>();
        String meuIP = getMeuIp();
        String templateIP = meuIP.substring(0, meuIP.lastIndexOf('.') + 1);

        try {

            for (int bloco = 0; bloco < 256; bloco++) {
                InetAddress endereco = InetAddress.getByName(templateIP + String.valueOf(bloco));

                if (endereco.isReachable(100)) {
                    todosIps.add(endereco.getHostAddress() + "/online");
                } else {
                    todosIps.add(endereco.getHostAddress() + "/offline");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return todosIps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button buttonRedeInterna = findViewById(R.id.homeBtn);
        Button buttonScan = findViewById(R.id.buttonScan);
        LinearLayout linearLayout = findViewById(R.id.layoutInfos);
        TextView listarIps = findViewById(R.id.listarIps);
        ProgressBar progressBar = findViewById(R.id.progressBarScanBtn);
        CheckBox checkboxOnlineOffline = findViewById(R.id.checkboxOnlineOffline);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    List<String> ips = getIpsRede();

                    runOnUiThread(() -> {
                        StringBuilder resultado = new StringBuilder();
                        String statusAlvo = checkboxOnlineOffline.isChecked() ? "online" : "offline";

                        for (String ip : ips) {
                            String[] partes = ip.split("/");

                            if (partes.length == 2) {
                                String oIp = partes[0];
                                String oStatus = partes[1];

                                if (oStatus.trim().equalsIgnoreCase(statusAlvo)) {
                                    resultado.append("IP: ")
                                            .append(oIp)
                                            .append("\n");
                                }
                            }
                        }

                        listarIps.setText(resultado);
                        progressBar.setVisibility(View.GONE);
                    });

                }).start();

            }
        });

        buttonRedeInterna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


    }
}