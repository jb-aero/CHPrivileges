package com.zeoldcraft.chprivileges;

import net.krinsoft.privileges.Privileges;
import net.krinsoft.privileges.groups.Group;
import com.laytonsmith.abstraction.MCPlugin;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;

public class CHPrivileges {
	
	@api
	public static class priv_get_groups extends privfunc {

		public ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.InvalidPluginException};
		}

		public Construct exec(Target t, Environment environment,
				Construct... args) throws ConfigRuntimeException {
			CArray ret = CArray.GetAssociativeArray(t);
			for (Group g : getPriv(t).getGroupManager().getGroups()) {
				CArray d = CArray.GetAssociativeArray(t);
				d.set("rank", new CInt(g.getRank(), t), t);
				d.set("promotion", new CString(g.getPromotion(), t), t);
				d.set("demotion", new CString(g.getDemotion(), t), t);
				ret.set(g.getName(), d, t);
			}
			return ret;
		}

		public String getName() {
			return "priv_get_groups";
		}

		public Integer[] numArgs() {
			return new Integer[]{0};
		}

		public String docs() {
			return "array {} Returns an array of all groups as keys containing an array of their"
					+ "rank, promotion, and demotion info.";
		}
	}
	
	@api
	public static class priv_user_get_group extends privfunc {

		public ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.InvalidPluginException};
		}

		public Construct exec(Target t, Environment environment,
				Construct... args) throws ConfigRuntimeException {
			Group g = getPriv(t).getPlayerManager().getPlayer(args[0].val()).getGroup();
			return new CString(g.getName(), t);
		}

		public String getName() {
			return "priv_user_get_group";
		}

		public Integer[] numArgs() {
			return new Integer[]{1};
		}

		public String docs() {
			return "string {player} Returns the name of the group a player is in";
		}
	}
	
	@api
	public static class priv_user_set_group extends privfunc {

		public ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.InvalidPluginException};
		}

		public Construct exec(Target t, Environment environment,
				Construct... args) throws ConfigRuntimeException {
			return new CString(getPriv(t).getGroupManager().setGroup(args[0].val(), args[1].val()).getName(), t);
		}

		public String getName() {
			return "priv_user_set_group";
		}

		public Integer[] numArgs() {
			return new Integer[]{2};
		}

		public String docs() {
			return "string {player, group} Sets a player as a member of a group, and returns the groupname."
					+ " Offline players work, so the name must be exact.";
		}
	}
	
	@api
	public static class priv_user_add_permission extends privfunc {

		public ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.InvalidPluginException};
		}

		public Construct exec(Target t, Environment environment,
				Construct... args) throws ConfigRuntimeException {
			String player = args[0].val();
			String perm = args[1].val();
			String world = null;
			if (args.length == 3) {
				world = args[2].val();
			}
			return new CBoolean(getPriv(t).getPlayerManager().getPlayer(player).addPermission(world, perm), t);
		}

		public String getName() {
			return "priv_user_add_permission";
		}

		public Integer[] numArgs() {
			return new Integer[]{2, 3};
		}

		public String docs() {
			return "boolean {player, permission, [world]} Adds a permission to a player, returns its success.";
		}
	}
	
	public static Privileges getPriv(Target t) {
		Static.checkPlugin("Privileges", t);
		MCPlugin p = Static.getServer().getPluginManager().getPlugin("Privileges");
		if (p.isInstanceOf(Privileges.class)) {
			return (Privileges) p;
		}
		throw new ConfigRuntimeException("Privileges plugin is invalid!", ExceptionType.InvalidPluginException, t);
	}

	private static abstract class privfunc extends AbstractFunction {
		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public CHVersion since() {
			return CHVersion.V3_3_1;
		}
	}
}
