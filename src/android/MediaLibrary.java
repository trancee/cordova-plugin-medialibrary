/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
*/
package org.apache.cordova.medialibrary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Uri;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;

public class MediaLibrary extends CordovaPlugin {
	private static final String LOG_TAG = "MediaLibrary";

	/**
	 * Sets the context of the Command. This can then be used to do things like
	 * get file paths associated with the Activity.
	 *
	 * @param cordova The context of the main Activity.
	 * @param webView The CordovaWebView Cordova is running in.
	 */
	@Override
	public void initialize (CordovaInterface cordova, CordovaWebView webView) {
		Log.v(LOG_TAG, "MediaLibrary: initialization");

		super.initialize (cordova, webView);
	}

	/**
	 * Executes the request and returns PluginResult.
	 *
	 * @param action			The action to execute.
	 * @param args			  JSONArry of arguments for the plugin.
	 * @param callbackContext   The callback id used when calling back into JavaScript.
	 * @return				  True if the action was valid, false otherwise.
	 */
	@Override
	public boolean execute (String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {
		Log.v(LOG_TAG, "Executing action: " + action);

		final Activity activity = this.cordova.getActivity ();
		final Window window = activity.getWindow ();

		if ("savePicture".equals (action)) {
			try {
				File file = new File (args.getString (0).replaceAll ("^file://", ""));

				MediaScannerConnection.scanFile (
					activity.getApplicationContext (),

					new String[] {
						file.getAbsolutePath ()
					}, 

					null,

					new MediaScannerConnection.OnScanCompletedListener () {
						@Override
						public void onScanCompleted (String path, Uri uri) {
							LOG.d(LOG_TAG, "-- savePicture::onScanCompleted path = " + path + ", uri = " + uri);
						}
					}
				);
			}
			catch (IllegalArgumentException e) {
				callbackContext.error ("Illegal Argument Exception");

				PluginResult r = new PluginResult(PluginResult.Status.ERROR);
				callbackContext.sendPluginResult(r);

				return true;
			}

			PluginResult r = new PluginResult (PluginResult.Status.NO_RESULT);
			r.setKeepCallback (true);
			callbackContext.sendPluginResult (r);

			return true;
		}

		return false;
	}
}
