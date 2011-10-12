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

import jxtras.mobility.widget.scroller.DraggableFastScroller.AdjustmentListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class FastScrollerDemo extends Activity {

	private DraggableFastScroller fastScroller;
	private ListView playList;	
	private Button btnArrawUp, btnArrawDown;
	private int itemHeight;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.playlist);
		fastScroller = (DraggableFastScroller) this.findViewById(R.id.fast_scroller);
		playList = (ListView) this.findViewById(R.id.play_list);
		btnArrawUp = (Button) this.findViewById(R.id.arrawUpBtn);
		btnArrawDown = (Button) this.findViewById(R.id.arrawDownBtn);

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
		
		fastScroller.setAdjustmentListener(new AdjustmentListener() {
			@Override
			public void onAdjustmentValueChanged(int value) {
				scrollList(value);
			}
		});
		
		final int unitIncrement = 10;
		btnArrawUp.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int offset = fastScroller.getValue() - unitIncrement;
				if (offset >= 0) {
					fastScroller.setValue(offset);
					scrollList(fastScroller.getValue());	
				}
			}			
		});
		
		btnArrawDown.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				int offset = fastScroller.getValue() + unitIncrement;
				if (offset <= fastScroller.getMax()) {
					fastScroller.setValue(offset);
					scrollList(fastScroller.getValue());	
				}
			}
		});
	}
	
	private void scrollList(int value) {
		int position = value / itemHeight;
		int y = value % itemHeight;
		Log.i("ddd", "value = " + value);
		Log.i("ddd", "position = " + position);
		Log.i("ddd", "y = " + y);
		playList.setSelectionFromTop(position, -y);
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
		String album;
		String artist;

		public Song(final String album, final String artist) {
			this.album = album;
			this.artist = artist;
		}
	}

	private ArrayList<Song> createSongList(final int size) {
		final ArrayList<Song> songs = new ArrayList<Song>();
		for (int i = 0; i < size; i++) {
			songs.add(new Song("Classic Music #" + i, "Jacky Zhang"));
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
			
			final TextView name = (TextView) view.findViewById(R.id.album_name);
			name.setText(getItem(position).album);

			final TextView number = (TextView) view.findViewById(R.id.artist_name);
			number.setText(getItem(position).artist);

			final ImageView photo = (ImageView) view.findViewById(R.id.art_work_image);
			photo.setImageDrawable(artworkImage);

			return view;
		}
	}
}