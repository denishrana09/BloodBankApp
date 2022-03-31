# BloodBankApp
Made for Emergency Situations

Technologies used : Firebase Database,Google MapsAPI

Description :
User have to login via gmail,add mobile number and blood group.
User can search other users who are available to donate blood.
App will give list of all users by searched query(ex.O+,B+) and shows distance between each person with sorted list.
User can then directly call the person from the list.

How It Works:
There is one background service which will update users location whenever user goes 1 km away from past location.
so that every users latest location will be available in Database.

<h1>Setup Steps</h1>

1.First of all you need google-services.json. Create a Firebase project in the <a href="https://console.firebase.google.com/">Firebase console</a>, if you don't already have one. Go to your project and click ‘Add Firebase to your Android app’. Follow the setup steps. At the end, you'll download a google-services.json file which you should add to your project.

![alt text](https://user-images.githubusercontent.com/7821425/32899277-30da3374-caf3-11e7-86e0-58cb1bfd59e2.png "Download google services json file")


2.Setup realtime database. In firebase console go to DEVELOP->Database-> Get Started -> choose tab ‘RULES’ and past this:
```
{
  "rules": {
    ".read": "auth!=null",
    ".write": "auth!=null"   
  }
 }
 ```


 3.If you haven't yet specified your app's SHA-1 fingerprint, do so from the Settings page <a href="https://console.firebase.google.com/u/0/project/_/settings/general/">Settings page</a> of the Firebase console. See <a href="https://developers.google.com/android/guides/client-auth">Authenticating Your Client</a> for details on how to get your app's SHA-1 fingerprint.
 
 4.Enable the sign in method with google. Go to DEVELOP -> Authentication -> SIGN-IN METHODS. You will see Sign-in providers. Find Google and enable it.


it is edit