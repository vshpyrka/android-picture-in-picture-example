# android-picture-in-picture
[PictureInPictureLauncherActivity](https://github.com/vshpyrka/android-picture-in-picture-example/blob/main/src/main/java/com/example/pip/PictureInPictureLauncherActivity.kt) - serves as a demonstration of launching a separate activity in Picture-In-Picture mode.

[PictureInPictureActivity](https://github.com/vshpyrka/android-picture-in-picture-example/blob/main/src/main/java/com/example/pip/PictureInPictureActivity.kt) - Activity that minimizes to a smaller size window on the screen or can be expanded back to a full size screen.

Example:

https://github.com/user-attachments/assets/2dd5e928-d1ae-4e63-aaa6-fe6067632510

Activity that is in Picture-In-Picture mode can be scaled with double tab action or stashed to the side of the screen. `onPictureInPictureUiStateChanged` method from Activity class is called by the system when PiP mode has state changes.

```
override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        super.onPictureInPictureUiStateChanged(pipState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (pipState.isStashed) {
              ...
```

Example:

https://github.com/user-attachments/assets/c82048db-9bba-4ab1-82ee-3ad51175df46

It's also possible to send a different intent arguments to the Picture-In-Picture activity when it's already launched.

Example:

https://github.com/user-attachments/assets/74b0511b-d64c-45e6-9116-dffbfba50ead

`PictureInPictureParams.Builder` class allows to add separate remote action icons to the activity that is moved to the picture-in-picture mode:

```
val builder = PictureInPictureParams.Builder()
builder.setActions(
    listOf(
        RemoteAction(
            android.graphics.drawable.Icon.createWithResource(context, R.drawable.ic_add_reaction),
            "Custom action", // title
            "Custom action", // content description
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                Intent(ACTION_BROADCAST_CONTROL)
                    .setPackage(context.packageName)
                    .putExtra(EXTRA_CONTROL_TYPE, CONTROL_TYPE),
                PendingIntent.FLAG_IMMUTABLE,
            ),
        )
    )
)
```

Example:

![Screenshot 2025-02-03 at 11 37 02](https://github.com/user-attachments/assets/a12dda40-e2ce-44bd-a9f6-54df333020d9)

More information about Picture-In-Picture mode: [Picture-In-Picture](https://developer.android.com/develop/ui/compose/system/picture-in-picture)
