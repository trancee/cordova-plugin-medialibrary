/*  
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/


using Microsoft.Phone.Shell;

using System;

using System.Collections;
using System.Collections.Generic;

using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.IO.IsolatedStorage;
using System.Threading;

using System.Windows;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Threading;

using Microsoft.Xna.Framework.Media;



namespace WPCordovaClassLib.Cordova.Commands
{
	public class MediaLibrary : BaseCommand
	{
		public void savePicture(string options)
		{	//exec(null, null, "MediaLibrary", "savePicture", [imageUri, albumName]);
			string[] args = JSON.JsonHelper.Deserialize<string[]>(options);

			string imageUri = args[0];
			string albumName = args[1];
			string fileName = args[2];

            Deployment.Current.Dispatcher.BeginInvoke(() =>
            {
                using (IsolatedStorageFile isoFile = IsolatedStorageFile.GetUserStoreForApplication())
                {
                    if (! isoFile.FileExists (imageUri))
                    {
                        DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "File does not exist."));
                        return;
                    }

                    using (Microsoft.Xna.Framework.Media.MediaLibrary mediaLibrary = new Microsoft.Xna.Framework.Media.MediaLibrary())
                    {
                        using (Stream fileStream = isoFile.OpenFile(imageUri, FileMode.Open))
                        {
                            var bitmapImage = new BitmapImage { CreateOptions = BitmapCreateOptions.None };
                            bitmapImage.SetSource(fileStream);

                            using (MemoryStream bitmapStream = new MemoryStream())
                            {
                                var writeableBitmap = new WriteableBitmap(bitmapImage);
                                writeableBitmap.SaveJpeg(bitmapStream, writeableBitmap.PixelWidth, writeableBitmap.PixelHeight, 0, 100);

                                bitmapStream.Seek(0, SeekOrigin.Begin);

                                // Image data that is passed in must be in a JPEG file format. SavePicture always saves the image in a JPEG file format.
                                var picture = mediaLibrary.SavePicture(fileName, bitmapStream);

                                if (picture.Name.Contains(fileName))
                                {
                                    DispatchCommandResult(new PluginResult(PluginResult.Status.OK, "Picture saved."));
                                    return;
                                }
                                else
                                {
                                    DispatchCommandResult(new PluginResult(PluginResult.Status.ERROR, "Picture not saved."));
                                    return;
                                }
                            }
                        }
                    }
                }
            });
		}
	}
}