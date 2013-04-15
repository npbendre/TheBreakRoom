package com.example.thebreakroom;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.example.model.User;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class UsersListActivity extends ListActivity {

	private ListView mListView;
	private GroupListAdapter mAdapter;
	private List<User> mGroups;
	private View mProgress;
	private EditText mEditText;
	private AlertDialog alert;
	private TimePicker mTimePicker;
	private String mGroupId;
	private ParseQuery query1;
	private EditText mEditTextOrder;
	private AlertDialog alertOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_list);
		setTitle("Users");

		mProgress = findViewById(R.id.progressBar1);
		// ((ViewGroup)getListView().getParent()).addView(emptyView);
		mListView = (ListView) findViewById(android.R.id.list);
		mGroupId = getIntent().getStringExtra("groupid");
		mGroups = new ArrayList<User>();
		
		// Set the ListAdapter
		mAdapter = new GroupListAdapter(this, mGroups);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				User g = (User) mListView.getItemAtPosition(position);
//				makeToast("Selected Group "+ g.getId() +"-"+g.getName());
			}

		});

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		Parse.initialize(this, "htzoiQfNYAeAXEd1JWRt8cyOwzDxtW0Eo0NcoNmD", "aG61XqnawPayAukgt3ReRPldVAlkgGInebyo1agj");
		Parse.initialize(this, "nSec4SOo4RDJhbMJhvX1JdcdAgg5VF0mQdkkdHKG", "1IMivB5ci1yc4QBSHeGjrYNltKmeU29VWQPjzzfx");
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
			Toast.makeText(this, "Refreshing list...", Toast.LENGTH_SHORT).show();
			refreshList();
			return true;
		case R.id.add_group:
			addGroupDialog();
			return true;
		case R.id.delete_user:
			deleteUserDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.users_list, menu);
		return true;
	}

	private class GroupListAdapter extends ArrayAdapter<User> {

		private List<User> mObjects;

		public GroupListAdapter(Context context, List<User> objects) {
			super(context, R.layout.group_list_item, android.R.id.text1,
					objects);
			mObjects = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View row = null;

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.group_list_item, parent, false);
			} else {
				row = convertView;
			}

			final User object = mObjects.get(position);

			TextView textView = (TextView) row
					.findViewById(R.id.file_picker_text);
			TextView desc = (TextView) row.findViewById(R.id.category_desc);
			ImageView img = (ImageView) row.findViewById(R.id.category_favorite);
			img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
//					addUser(object.getId());
				}
			});
			// Set single line
			textView.setSingleLine(true);

			textView.setText(object.getName());
			img.setVisibility(View.GONE);
			if(object.getOrder() != null && object.getOrder().length() > 0){
				desc.setText("He's getting "+object.getOrder());
			}else{
				desc.setText("No order yet");
			}

			return row;
		}

	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(alertOrder!=null){
			alertOrder.dismiss();
		}
		if(alert!=null){
			alert.dismiss();
		}
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
                       alert.dismiss();
                   }
               })
               .setPositiveButton("Create", new DialogInterface.OnClickListener() {
            	   @Override
            	   public void onClick(DialogInterface dialog, int id) {
            		   addUserFromMenu(mEditTextOrder.getText().toString());
                   }
               });      
       alertOrder =  builder.create();
       alertOrder.show();
		
	}


	private void addGroupDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
//        LayoutInflater inflater = getLayoutInflater(); 	   
// 	    View view = inflater.inflate(R.layout.create_folder_dialog, null);
// 	    mEditText = (EditText) view.findViewById(R.id.folder_name);
// 	    mTimePicker = (TimePicker)view.findViewById(R.id.timePicker1);
// 	    mTimePicker.setVisibility(View.GONE);
        builder
        	   .setTitle("Are you sure you want to join?")
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       alert.dismiss();
                   }
               })
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            	   @Override
            	   public void onClick(DialogInterface dialog, int id) {
            		   ParseQuery query = new ParseQuery("LunchGroup");
           			
       				ParseObject grp;
       				try {
       					grp = query.get(mGroupId);
       					if(grp.getBoolean("takeOut")){
       						addOrderDialog(mGroupId);
       					}else{
       						addUserFromMenu("Dine In/Catering");
       					}
       				}catch (ParseException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            			

                   }

				
               });      
       alert =  builder.create();
       alert.show();
		
	}
	
	private void addUserFromMenu(String order) {
		ParseQuery query = new ParseQuery("LunchGroup");
			
				ParseObject grp;
				try {
					grp = query.get(mGroupId);
    				ParseRelation users = grp.getRelation("members");
    				ParseUser user = ParseUser.getCurrentUser();
    						user.put("order", order);
    						user.saveInBackground();
    				users.add(user);
					makeToast("Adding user...");
    				grp.saveInBackground(new SaveCallback() {
    					
    					@Override
    					public void done(ParseException e) {
    						if(e == null){
    							refreshList();
    						}
    					}
    				});
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	
private void deleteUserDialog() {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
//        LayoutInflater inflater = getLayoutInflater(); 	   
// 	    View view = inflater.inflate(R.layout.create_folder_dialog, null);
// 	    mEditText = (EditText) view.findViewById(R.id.folder_name);
// 	    mTimePicker = (TimePicker)view.findViewById(R.id.timePicker1);
// 	    mTimePicker.setVisibility(View.GONE);
        builder
        	   .setTitle("Are you sure you want to leave?")
               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                       alert.dismiss();
                   }
               })
               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            	   @Override
            	   public void onClick(DialogInterface dialog, int id) {
            		   ParseQuery query = new ParseQuery("LunchGroup");
            			
            				ParseObject grp;
							try {
								grp = query.get(mGroupId);
	            				ParseRelation users = grp.getRelation("members");
	            				users.remove(ParseUser.getCurrentUser());
	    						makeToast("Deleting user...");
	            				grp.saveInBackground(new SaveCallback() {
	            					
	            					@Override
	            					public void done(ParseException e) {
	            						if(e == null){
	            							refreshList();
	            						}
	            					}
	            				});
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
            			
                   }
               });      
       alert =  builder.create();
       alert.show();
		
	}
	
	public static Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
	
	public void makeToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}
	
	public void addUser(String id, String order){
		ParseQuery query = new ParseQuery("User");
		try {
			ParseObject obj = query.get(id);
			ParseRelation members = obj.getRelation("members");
			ParseUser user = ParseUser.getCurrentUser();
			user.put("order", order);
			user.saveInBackground();
			members.add(user);
			makeToast("Adding user...");
			obj.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException e) {
					if(e == null){
						refreshList();
					}
				}
			});
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshList() {

		try{
		mGroups.clear();
 
		ParseQuery query = new ParseQuery("LunchGroup");
		try {
			ParseObject grp = query.get(mGroupId);
			ParseRelation users = grp.getRelation("members");
			query1 = users.getQuery();
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		query1.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {
					for (ParseObject obj : objects) {
						String name = obj.getString("username");
						String order = obj.getString("order");
						String id = obj.getObjectId();
											
						User g = new User();
//						g.setDate(date);
						g.setName(name);
						g.setOrder(order);
						g.setId(id);
//						g.setCount(count);
						mGroups.add(g);
					}
//					Collections.sort(mGroups, new GroupComparator());
					mProgress.setVisibility(View.GONE);
//					mListView.setEmptyView(emptyView);
					mAdapter.notifyDataSetChanged();
				}

			}
		});
		}catch (Exception e){
			
		}
	}

	private class GroupComparator implements Comparator<User> {
		@Override
		public int compare(User f1, User f2) {
			return f1.getName().compareToIgnoreCase(f2.getName());
		}
	}
	
	

}
