
![[consenta.me](https://consenta.me) logo](img/consentame_logo.png)

# consentame-android [![](https://jitpack.io/v/consentame/consentame-android.svg)](https://jitpack.io/#consentame/consentame-android)
**Android plugin for [consenta.me](https://consenta.me).** Created May 2018 by Andrea Arighi <[andrea@chino.io](mailto:andrea@chino.io)>

Consenta.me is a [Chino.io](https://chino.io) product, created with all the experience on GDPR
and creation of health applications.

## How to use

*We provide some sample code to show how the plugin is meant to be be used. Check it out: [consentame-android-example](https://github.com/consentame/consentame-android-example)*

### Create a Consent
Consenta.me provides easy and GDPR/HIPAA-compliant consent tracking for web sites and apps.
Subscribe to the newsletter on [consenta.me](https://consenta.me) in order to get early access to the service.

The Consenta.me console provides an admin interface to create a Consent, specifying *Purposes*
and *Data Controllers* in an easy way. Once created, the Consent will be identified by a **Consent ID**.

### Install the Android library
*Instructions below are for* ***Android Studio 3.x*** *and* ***Android Gradle plugin 3.*** *The minimum supported SDK version is* **API 22 (Android 5.1)**

You can get the Consenta.me Android plugin via [jitpack](jitpack.io): [![](https://jitpack.io/v/consentame/consentame-android.svg)](https://jitpack.io/#consentame/consentame-android)

You need to edit your app's `build.gradle` file; add the maven repository URL:

```
allprojects {
	repositories {
		// other repositories . . .
		maven { url 'https://jitpack.io' }
	}
}
```

and the dependency:

```
dependencies {
	// other dependencies . . . 
	implementation 'com.github.consentame:consentame-android:0.1'
}
```


### Insert Consent button
You can now insert the `ConsentaMeCheckButton` in your Activities:

```XML
<me.consenta.android.consentame.ConsentaMeCheckButton
            android:id="@+id/consentame_btn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:consentId="PASTE HERE your Consent ID"
            app:dev="true"/>     
```

Once the button is set, you can control it with the class `ConsentaMe`, which provides a simple interface to get information about the status of the button:
```Java
ConsentaMe consentaMe = new ConsentaMe(this, R.id.consentame_btn);
```
Class `ConsentaMe` provides the following interface:
    * `public String getConsentId()`: returns the Consent ID that was set in the `consentId` XML attribute during step 3.
    * `public boolean isChecked()`: returns `true` if the user has approved the Consent.
    * `public String getUserConsentId()`: when a user approves a Consent, a unique ID is generated that binds that user
        with the approved Consent. This ID is called the **User Consent ID** and can be retrieved with this method.
        If the user has not approved the Consent, this will return `null`.
    * `public static String getCurrentConsentId()`, `public static boolean isCurrentChecked()` and `public static String getCurrentUserConsentId()`
        work like the previous methods, but they get information from the **currently running instance** of the Consent button,
        which exists as long as a User is reading the Consent's details.
    * Finally you can use `public static boolean isRunning()` to check whether there is an active instance.


***Note*** - remember to:

1. Import the `res-auto` namespace in the root Layout with `xmlns:app="http://schemas.android.com/apk/res-auto"`

2. Paste the Consent ID of the Consent you created on Consenta.me in the attribute `consentId`. This can not be changed (every button must have exactly one Consent ID, but you can have more buttons)

4. Right now only the develop API are supported, thus you should set the attribute `app:dev="true"` for the plugin to work.

## Button usage guidelines

* **You may NOT:**
    * Have more than one screen with Consent details running at a time. The user must approve a Consent (or exit the details screen)
    before opening another Consent.
    * Insert Views inside the button's layout (even though the `ConsentaMeCheckButton` is a `Layout`).
    * Change the code of the library and/or the behaviour of any of its classes.
    * Change the default `View.OnClickListener` and `onClick()` method of the button (trying to do so will result in an Exception)

* **You may:**
    * Add more than one `ConsentaMeCheckButton` in the same app / activity (as long as the user can open only one of them at a time).

* **Please do not alter the button's layout.** The mandatory `android:layout_width` and `android:layout_height` attributes should always be set to the following values:
  ```XML
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
  ```
  You **may not** add `android:padding` (and variants like `paddingLeft`,`paddingTop`, etc...), since it will break the button's layout.
  You **may** add `android:layout_margin` though (as well as its variants, e.g. `layout_marginLeft`,`layout_marginTop`, etc..),
  as long as all of its elements (the checkbox, the text and the [consenta.me](https://consenta.me)
  logo) are clearly visible, like in the image below:

  ![(img/button_preview.png)](img/button_preview.png)

  If you are working on very big screens (TV, tablet) or very small ones (e.g. wearables) and have troubles fitting in the button
  correctly, please contact [info@chino.io](mailto:info@chino.io) or [andrea@chino.io](mailto:andrea@chino.io) to discuss a solution.
