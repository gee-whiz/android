/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mobile.ui.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.ThrowableLoader;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.ui.ItemListFragment;
import org.eclipse.egit.github.core.User;
import com.github.mobile.R.string;
import com.github.mobile.ui.user.UserListAdapter;
import com.github.mobile.ui.user.UserViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static android.app.SearchManager.QUERY;

/**
 * Fragment to display the users from a search result
 */
public class SearchUserListFragment extends ItemListFragment<User> {

    private String query;

    @Inject
    private UserService service;

    @Inject
    private AvatarLoader avatars;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(string.no_people);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        query = getStringExtra(QUERY);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<List<User>>(getActivity(), items) {

            @Override
            public List<User> loadData() throws Exception {
                List<User> users = new ArrayList<User>();
                User user = service.getUser(query);
                if (user != null)
                    users.add(user);
                return users;
            }
        };
    }

    @Override
    protected SingleTypeAdapter<User> createAdapter(List<User> items) {
        User[] users = items.toArray(new User[items.size()]);
        return new UserListAdapter(getActivity().getLayoutInflater(), users,
                avatars);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        User user = (User) l.getItemAtPosition(position);
        if (!AccountUtils.isUser(getActivity(), user))
            startActivity(UserViewActivity.createIntent(user));
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return string.error_users_search;
    }
}
