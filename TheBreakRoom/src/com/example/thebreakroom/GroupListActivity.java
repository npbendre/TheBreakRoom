package com.example.thebreakroom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.example.model.Group;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class GroupListActivity extends ListActivity {

	private ListView mListView;
	private GroupListAdapter mAdapter;
	private List<Group> mGroups;
	private View mProgress;
	private EditText mEditText;
	private AlertDialog alert;
	private TimePicker mTimePicker;
	private OnClickProgressDialog mProgressBar;
	private Spinner mSpinner;
	private AlertDialog alertOrder;
	private EditText mEditTextOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_list);
		setTitle("Groups");

		mProgress = findViewById(R.id.progressBar1);
		// ((ViewGroup)getListView().getParent()).addView(emptyView);
		mListView = (ListView) findViewById(android.R.id.list);

		mGroups = new ArrayList<Group>();
		
		// Set the ListAdapter
		mAdapter = new GroupListAdapter(this, mGroups);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Group g = (Group) mListView.getItemAtPosition(position);
				startGroupActivity(g.getId());
//				makeToast("Selected Group "+ g.getId() +"-"+g.getName());
			}

		});

		Parse.initialize(this, "nSec4SOo4RDJhbMJhvX1JdcdAgg5VF0mQdkkdHKG", "1IMivB5ci1yc4QBSHeGjrYNltKmeU29VWQPjzzfx");
	}


	public void startGroupActivity(String id) {
		Intent urlIntent = new Intent(this, UsersListActivity.class);
		 urlIntent.putExtra("groupid", id);
		this.startActivity(urlIntent);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		Parse.initialize(this, "htzoiQfNYAeAXEd1JWRt8cyOwzDxtW0Eo0NcoNmD", "aG61XqnawPayAukgt3ReRPldVAlkgGInebyo1agj");
//		ParseUser.enableAutomaticUser();
		refreshList();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
//		case android.R.id.home:
//			showHomeView();
//			return true;
		case R.id.refresh_group:
			showProgress();
			Toast.makeText(this, "Refreshing list...", Toast.LENGTH_SHORT).show();
			refreshList();
			return true;
		case R.id.add_group:
			addGroupDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	static class ViewHolder {
		ImageView img;
		TextView name;
		TextView desc;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.group_list, menu);
		return true;
	}

	private class GroupListAdapter extends ArrayAdapter<Group> {

		private List<Group> mObjects;

		public GroupListAdapter(Context context, List<Group> objects) {
			super(context, R.layout.group_list_item, android.R.id.text1,
					objects);
			mObjects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder;

			// Check if the incoming view is null.
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.group_list_item, parent, false);
				holder = new ViewHolder();
				holder.name = (TextView)convertView.findViewById(R.id.file_picker_text);
				holder.desc = (TextView)convertView.findViewById(R.id.category_desc);
				holder.img = (ImageView)convertView.findViewById(R.id.category_favorite);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			
			
			Group obj = new Group();
				if(position < mObjects.size()){
					obj = mObjects.get(position);
				}else{
					return convertView;					
				}
			final Group object = obj;	
			
			holder.img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showProgress();
					String desc = "Dine In/Catering";
					if(object.isTakeOutflag()){
						addOrderDialog(object.getId());
					}else{
					addUser(object.getId(), desc);
					}
				}
			});
			// Set single line
			holder.name.setSingleLine(true);

			holder.name.setText(object.getName() + " ("+object.getCount()+")");
			holder.desc.setText("Schedule Time: "+object.getDate().toLocaleString());

			return convertView;
		}

	}


	private void addGroupDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        LayoutInflater inflater = getLayoutInflater(); 	   
 	    View view = inflater.inflate(R.layout.create_folder_dialog, null);
 	    mEditText = (EditText) view.findViewById(R.id.folder_name);
 	    mTimePicker = (TimePicker)view.findViewById(R.id.timePicker1);
 	    mSpinner = (Spinner)view.findViewById(R.id.spinner1);
        builder.setView(view)
        	   .setTitle("Create Group")
               .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       alert.dismiss();
                   }
               })
               .setPositiveButton("Create", new DialogInterface.OnClickListener() {
            	   @Override
            	   public void onClick(DialogInterface dialog, int id) {
            		   showProgress();
            		   ParseObject obj = new ParseObject("LunchGroup");
            		   String value = mEditText.getText().toString();
            		   int hour = mTimePicker.getCurrentHour();
            		   int time = mTimePicker.getCurrentMinute();
            		   boolean flag = String.valueOf(mSpinner.getSelectedItem()).equalsIgnoreCase("Take Out");
            		   Date date = new Date();
            		   Date sch = new Date(date.getYear(),3,14,hour,time);
            		   obj.put("name", value);
            		   obj.put("time", sch);
            		   obj.put("maxMembers", 4);
            		   obj.put("takeOut", flag);
            		   
            		   obj.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException e) {
							if(e == null){
								refreshList();
							}else{
								dismissProgress();
								makeToast("Error: "+e.getMessage());
							}
							
						}
					});

						makeToast("Creating group...");
                   }
               });      
       alert =  builder.create();
       alert.show();
		
	}
	
private void addOrderDialog(final String grpid) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        LayoutInflater inflater = getLayoutInflater(); 	   
 	    View view = inflater.inflate(R.layout.addorder, null);
 	    mEditTextOrder = (EditText) view.findViewById(R.id.folder_name);
        builder.setView(view)
        	   .setTitle("Add Order Item")
               .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   if(alertOrder!=null)
                		   alertOrder.dismiss();
                   }
               })
               .setPositiveButton("Create", new DialogInterface.OnClickListener() {
            	   @Override
            	   public void onClick(DialogInterface dialog, int id) {
            		   addUser(grpid, mEditTextOrder.getText().toString());
                   }
               });      
       alertOrder =  builder.create();
       alertOrder.show();
	   dismissProgress();
		
	}
	
	public void makeToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	public void addUser(String id, String order){
		ParseQuery query = new ParseQuery("LunchGroup");
		try {

			makeToast("Adding user...");
			ParseObject obj = query.get(id);
			ParseRelation members = obj.getRelation("members");
			ParseUser user = ParseUser.getCurrentUser();
			user.put("order", order);
			user.saveInBackground();
			members.add(user);
			obj.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					if(e == null){
						refreshList();
					}else{
						dismissProgress();
					}
					
				}
			});
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshList() {

		mGroups.clear();
 
		ParseQuery query = new ParseQuery("LunchGroup");
		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {

				dismissProgress();
				
				if (e == null) {
					for (ParseObject obj : objects) {
						String name = obj.getString("name");
						String id = obj.getObjectId();
						Date date = obj.getDate("time");
						ParseRelation members = obj.getRelation("members");
						boolean flag = obj.getBoolean("takeOut");
						int count;
						try {
							count = members.getQuery().count();
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							count = -1;
						}
						
						Group g = new Group();
						g.setDate(date);
						g.setName(name);
						g.setId(id);
						g.setCount(count);
						g.setTakeOutflag(flag);
						mGroups.add(g);
					}
					Collections.sort(mGroups, new GroupComparator());
					mProgress.setVisibility(View.GONE);
//					mListView.setEmptyView(emptyView);
					mAdapter.notifyDataSetChanged();
				}

			}
		});
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(alert != null)
			alert.dismiss();
		if(alertOrder != null)
			alertOrder.dismiss();
		
		dismissProgress();
	}
	
	private class GroupComparator implements Comparator<Group> {
		@Override
		public int compare(Group f1, Group f2) {
			return f1.getName().compareToIgnoreCase(f2.getName());
		}
	}
	
	public void showProgress() {
		if(mProgressBar == null){		
			mProgressBar = OnClickProgressDialog.show(this, null, null, true, false);
		}else{
			mProgressBar.show();
		}
	}

	public void dismissProgress() {
		if(mProgressBar != null){
			mProgressBar.dismiss();
		}		
	}

}
