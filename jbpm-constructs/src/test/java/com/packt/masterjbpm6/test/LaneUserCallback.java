package com.packt.masterjbpm6.test;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.task.UserGroupCallback;

public class LaneUserCallback implements UserGroupCallback {
	public boolean existsUser(String userId) {
		return true;
	}

	public boolean existsGroup(String groupId) {
		return true;
	}

	public List<String> getGroupsForUser(String userId, List<String> groupIds,
			List<String> allExistingGroupIds) {
		List<String> groups = new ArrayList<String>();
		if (userId.equalsIgnoreCase("luigi")) {
			groups.add("pizzerianapoli");
		}
		if (userId.equalsIgnoreCase("mario")) {
			groups.add("pizzerianapoli");
		}
		return groups;
	}

}
