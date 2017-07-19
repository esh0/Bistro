package pl.sportdata.beestro.modules.sync;

import android.os.AsyncTask;
import android.os.Bundle;

import pl.sportdata.beestro.entities.DataProviderFactory;

public class CleanDatabaseDialogFragment extends SyncDialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new CleanTask().execute();
    }

    class CleanTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DataProviderFactory.getDataProvider(getActivity()).cleanSyncObject(CleanDatabaseDialogFragment.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onSyncFinished(null);
        }
    }
}
