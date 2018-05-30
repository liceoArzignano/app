package it.liceoarzignano.foundation.db

import android.os.AsyncTask

abstract class DatabaseTask<I, O>(protected var db: AppDatabase) : AsyncTask<I, Void, O>()