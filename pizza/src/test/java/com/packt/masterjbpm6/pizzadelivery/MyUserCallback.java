package com.packt.masterjbpm6.pizzadelivery;

import java.util.List;

import org.kie.api.task.UserGroupCallback;

public class MyUserCallback implements UserGroupCallback {
	public boolean existsUser(String userId) {
		return true;
	}

	public boolean existsGroup(String groupId) {
		return true;
	}

	public List<String> getGroupsForUser(String userId, List<String> groupIds,
			List<String> allExistingGroupIds) {
		// List<String> groups = new ArrayList();
		// if (userId.equals("mary")) {
		// groups.add("Administrators");
		// }
		// if (userId.equals("nino")) {
		// groups.add("Administrators");
		// }
		// return groups;
		return allExistingGroupIds;
	}

}
