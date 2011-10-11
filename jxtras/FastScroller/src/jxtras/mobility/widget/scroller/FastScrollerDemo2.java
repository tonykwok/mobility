/*
 * Copyright (c) 2011 mobility.googlecode.com. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of jxtras.mobility nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package jxtras.mobility.widget.scroller;

import java.util.ArrayList;

import jxtras.mobility.widget.scroller.FastScroller.OnAdjustListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class FastScrollerDemo2 extends Activity {

	private FastScroller fastScroller;
	private ListView playList;	
	private int itemHeight;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		fastScroller = (FastScroller) this.findViewById(R.id.fast_scroller);
		playList = (ListView) this.findViewById(R.id.play_list);

		final ArrayList<Song> contacts = createSongList(20);
		playList.setAdapter(new MyAdapter(this, contacts));
		// Hide the Scollbar
		playList.setVerticalScrollBarEnabled(false);
		playList.setHorizontalScrollBarEnabled(false);
		//
		playList.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (visibleItemCount > 0) {
					View v = view.getChildAt(0);
					int top = (v == null) ? 0 : v.getTop();			
					int offset = firstVisibleItem * itemHeight - top;
					Log.d("ddd", "firstVisibleItem = " + firstVisibleItem);
					Log.d("ddd", "offset = " + offset);
					fastScroller.setValue(offset);					
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		fastScroller.setAdjustmentListener(new OnAdjustListener() {
			@Override
			public void onAdjustmentValueChanged(int value) {
				int position = value / itemHeight;
				int y = value % itemHeight;
				Log.i("ddd", "value = " + value);
				Log.i("ddd", "position = " + position);
				Log.i("ddd", "y = " + y);
				playList.setSelectionFromTop(position, -y);
			}
		});
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		int itemCount = playList.getAdapter().getCount();
		int childCount = playList.getChildCount();
		if (childCount > 0) {
			itemHeight = playList.getChildAt(0).getMeasuredHeight();
			int maxScrollAmount = itemCount * itemHeight - playList.getMeasuredHeight();
			Log.d("ddd", "maxScrollAmount = " + maxScrollAmount);
			Log.d("ddd", "itemHeight = " + itemHeight);
			Log.d("ddd", "itemCount = " + itemCount);
			Log.d("ddd", "playList.getMeasuredHeight() = " + playList.getMeasuredHeight());
			fastScroller.setMax(maxScrollAmount);
		}
	}


	private static class Song {
		String mName;
		String mNumber;

		public Song(final String name, final String number) {
			mName = name;
			mNumber = number;
		}
	}

	private ArrayList<Song> createSongList(final int size) {
		final ArrayList<Song> songs = new ArrayList<Song>();
		for (int i = 0; i < size; i++) {
			songs.add(new Song("Contact Number " + i, "+86(0)" + (int) (1000000 + 9000000 * Math.random())));
		}
		return songs;
	}

	private static class MyAdapter extends ArrayAdapter<Song> {

		private final Drawable artworkImage;

		public MyAdapter(final Context context, final ArrayList<Song> contacts) {
			super(context, 0, contacts);
			artworkImage = context.getResources().getDrawable(R.drawable.music);
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(getContext()).inflate(R.layout.list_item, null);
			}
			
			final TextView name = (TextView) view.findViewById(R.id.contact_name);
			name.setText(getItem(position).mName);

			final TextView number = (TextView) view.findViewById(R.id.contact_number);
			number.setText(getItem(position).mNumber);

			final ImageView photo = (ImageView) view.findViewById(R.id.contact_photo);
			photo.setImageDrawable(artworkImage);

			return view;
		}
	}
}