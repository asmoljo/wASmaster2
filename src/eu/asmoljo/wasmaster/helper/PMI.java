package eu.asmoljo.wasmaster.helper;

import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSCountStatistic;
import com.ibm.websphere.pmi.stat.WSRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.websphere.pmi.stat.WSTimeStatistic;

public class PMI {

	public static WSRangeStatistic getRangedStats(AdminClient client,ObjectName perf, ObjectName mbean, int stat) throws JMException,ConnectorException {
		WSStats wsStats = getWSStats(client, perf, mbean);
		return (WSRangeStatistic) wsStats.getStatistic(stat);
	}

	public static WSTimeStatistic getTimedStats(AdminClient client,ObjectName perf, ObjectName mbean, int stat) throws JMException,ConnectorException {
		WSStats wsStats = getWSStats(client, perf, mbean);
		return (WSTimeStatistic) wsStats.getStatistic(stat);
	}

	public static WSBoundedRangeStatistic getBoundedStats(AdminClient client,ObjectName perf, ObjectName mbean, int stat) throws JMException,ConnectorException {
		WSStats wsStats = getWSStats(client, perf, mbean);
		return (WSBoundedRangeStatistic) wsStats.getStatistic(stat);
	}

	public static WSCountStatistic getCountStats(AdminClient client,ObjectName perf, ObjectName mbean, int stat) throws JMException,ConnectorException {
		WSStats wsStats = getWSStats(client, perf, mbean);
		return (WSCountStatistic) wsStats.getStatistic(stat);
	}

	private static WSStats getWSStats(AdminClient adminClient,ObjectName perf, ObjectName mBean)throws InstanceNotFoundException, MBeanException,ReflectionException, ConnectorException {
		String[] signature = new String[] { "javax.management.ObjectName","java.lang.Boolean" };
		Object[] params = new Object[] { mBean, Boolean.FALSE };
		WSStats wsStats = (WSStats) adminClient.invoke(perf, "getStatsObject",params, signature);

		if (wsStats == null) {
			System.out.println("Ne moze se kreirati WSStats objekt!");
			throw new RuntimeException("Failed to get stats object on "+ mBean.getCanonicalName() + ".");
		}
		return wsStats;
	}

}
