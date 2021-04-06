package com.example.boroodat.general;

import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.boroodat.R;
import com.example.boroodat.adapter.Activity7_Adapter;
import com.example.boroodat.database.Activity7_DB;
import com.example.boroodat.databinding.A6Add1Binding;
import com.example.boroodat.databinding.A7AddBinding;
import com.example.boroodat.model.Activity7_Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.realm.Realm;
import io.realm.RealmResults;

public class Account
{
    private List<Activity7_Model> models =new ArrayList<>(  );
    private Activity7_Adapter adapter;
    private Context context;
    private android.app.AlertDialog progressDialog;
    private Realm realm;
    private String from;

    public Account(Context context,String from)
    {
        this.context = context;
        this.from=from;

        //--------------------------------------------

        realm=Realm.getDefaultInstance();

        progressDialog = new SpotsDialog(context, R.style.Custom);
        progressDialog.setCancelable(false);
    }

    public Account()
    {
        realm=Realm.getDefaultInstance();
    }

    public void dialog(TextView accountName, TextView accountId)
    {
        final A6Add1Binding binding1 = A6Add1Binding.inflate(LayoutInflater.from(context));
        View view = binding1.getRoot();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);

        //----------------------------------------------------------------------------------------------------------

        alertDialogBuilder.setCancelable(false);

        if (from.equals("manager"))
            alertDialogBuilder.setPositiveButton("ایجاد حساب جدید", null);

        alertDialogBuilder.setNeutralButton("لغو", null);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        //----------------------------------------------------------------------------------------------------------

        binding1.title.setText("لیست حساب ها");

        //----------------------------------------------------------------------------------------------------------

        binding1.lnr2.setVisibility(View.GONE);
        binding1.search.setVisibility(View.GONE);

        //----------------------------------------------------------------------------------------------------------

        progressDialog = new SpotsDialog(context, R.style.Custom);
        progressDialog.setCancelable(false);

        //----------------------------------------------------------------------------------------------------------

        adapter = new Activity7_Adapter(models, context,2,accountName,accountId,alertDialog,from );
        binding1.recyclerView.setLayoutManager ( new LinearLayoutManager( context ) );
        binding1.recyclerView.setAdapter (adapter);
        addAccount();

        //----------------------------------------------------------------------------------------------------------
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                Button add = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                add.setTextColor(context.getResources().getColor(R.color.black));
                add.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (new Internet(context).check())
                        {
                            addAccountDialog();
                        }
                        else
                            new Internet(context).enable();
                    }
                });


                Button cancel = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                cancel.setTextColor(context.getResources().getColor(R.color.black));
                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        //----------------------------------------------------------------------------------------------------------

        alertDialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_linear));
        alertDialog.show();
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        width = (int) ((width) * ((double) 4 / 5));
        alertDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void addAccountDialog()
    {
        final A7AddBinding binding1 = A7AddBinding.inflate(LayoutInflater.from(context));
        View view = binding1.getRoot();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(view);

        //----------------------------------------------------------------------------------------------------------

        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("تایید", null);
        alertDialogBuilder.setNeutralButton("لغو", null);
        final AlertDialog alertDialog = alertDialogBuilder.create();

        //----------------------------------------------------------------------------------------------------------

        binding1.balance.addTextChangedListener(new NumberTextWatcherForThousand(binding1.balance));
        binding1.title.addTextChangedListener(new ClearError(binding1.til1));
        
        //----------------------------------------------------------------------------------------------------------

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialogInterface)
            {
                Button add = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                add.setTextColor(context.getResources().getColor(R.color.black));
                add.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (binding1.title.getText().toString().equals(""))
                            binding1.til1.setError("عنوان حساب را وارد کنید.");

                        else
                        {
                            String accountNumber1="-";
                            String balance1="0";

                            if (!binding1.balance.getText().toString().equals(""))
                                balance1= NumberTextWatcherForThousand.trimCommaOfString(binding1.balance.getText().toString());

                            if (!binding1.accountNumber.getText().toString().equals(""))
                                accountNumber1=binding1.accountNumber.getText().toString();

                            if (new Internet(context).check())
                            {
                                createAccount(binding1.title.getText().toString(),accountNumber1,balance1,alertDialog);
                            }
                            else
                                new Internet(context).enable();

                        }
                    }
                });


                Button cancel = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                cancel.setTextColor(context.getResources().getColor(R.color.black));
                cancel.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        alertDialog.dismiss();
                    }
                });
            }
        });

        //--------------------------------------------------------------------------------------------------

        alertDialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rounded_linear));
        alertDialog.show();
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int width = display.widthPixels;
        width = (int) ((width) * ((double) 4 / 5));
        alertDialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void createAccount(final String title, final String account_number, final String balance, final AlertDialog alertDialog)
    {
        String url = context.getString(R.string.domain) + "api/account/create";
        progressDialog.show();

        JSONObject object = new JSONObject();
        try
        {
            object.put("company_id", new User_Info().company_id());
            object.put("title", title);
            object.put("account_number", account_number);
            object.put("balance", balance);
            object.put("date",new TodayDate().get());
            object.put("secret_key", context.getString(R.string.secret_key));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    int id = Integer.parseInt(response.getString("id"));
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(new Activity7_DB(id, title, account_number, balance));
                    realm.commitTransaction();

                    new Report().deposit(balance,"i");
                    //----------------------------------------------------

                    progressDialog.dismiss();
                    Toast.makeText(context, "ایجاد حساب با موفقیت انجام شد.", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();

                    addAccount();

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                Toast.makeText(context, "مجددا تلاش کنید.", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        };


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object, listener, errorListener)
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer "+ new User_Info().token());
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(3000, 1, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        AppController.getInstance().addToRequestQueue(request);

    }

    public void addAccount()
    {
        RealmResults<Activity7_DB> res = realm.where(Activity7_DB.class).findAll();

        models.clear();

        for (int i=0;i<res.size();i++)
        {
            models.add(new Activity7_Model(res.get(i).getId(),res.get(i).getTitle(),res.get(i).getAccountNumber(),res.get(i).getBalance()));
        }

        adapter.notifyDataSetChanged();
    }

    public void increase(String account_id,String amount)
    {
        RealmResults<Activity7_DB> results=realm.where(Activity7_DB.class).equalTo("id",Integer.parseInt(account_id)).findAll();

        if (results.size()>0)
        {
            double balance=Double.parseDouble(results.get(0).getBalance());
            balance=balance + Double.parseDouble(amount);

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(new Activity7_DB(results.get(0).getId(),results.get(0).getTitle(),results.get(0).getAccountNumber(), Math.round(balance)+""));
            realm.commitTransaction();
        }
    }

    public void decrease(String account_id,String amount)
    {
        RealmResults<Activity7_DB> results=realm.where(Activity7_DB.class).equalTo("id",Integer.parseInt(account_id)).findAll();

        if (results.size()>0)
        {
            double balance=Double.parseDouble(results.get(0).getBalance());
            balance=balance - Double.parseDouble(amount);

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(new Activity7_DB(results.get(0).getId(),results.get(0).getTitle(),results.get(0).getAccountNumber(), Math.round(balance)+""));
            realm.commitTransaction();
        }
    }
}
