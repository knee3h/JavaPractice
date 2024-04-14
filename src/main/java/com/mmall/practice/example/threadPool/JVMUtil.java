package com.mmall.practice.example.threadPool;


import org.apache.commons.lang.StringUtils;

import java.io.OutputStream;
import java.lang.Thread.State;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class JVMUtil {
    public JVMUtil() {
    }

    public static void jstack(OutputStream stream) throws Exception {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] var2 = threadMxBean.dumpAllThreads(true, true);
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ThreadInfo threadInfo = var2[var4];
            stream.write(getThreadDumpString(threadInfo).getBytes());
        }

    }

    private static String getThreadDumpString(ThreadInfo threadInfo) {
        StringBuilder sb = new StringBuilder("\"" + threadInfo.getThreadName() + "\" Id=" + threadInfo.getThreadId() + " " + threadInfo.getThreadState());
        if (threadInfo.getLockName() != null) {
            sb.append(" on " + threadInfo.getLockName());
        }

        if (threadInfo.getLockOwnerName() != null) {
            sb.append(" owned by \"" + threadInfo.getLockOwnerName() + "\" Id=" + threadInfo.getLockOwnerId());
        }

        if (threadInfo.isSuspended()) {
            sb.append(" (suspended)");
        }

        if (threadInfo.isInNative()) {
            sb.append(" (in native)");
        }

        sb.append('\n');
        int i = 0;
        int jstackMaxLine = 32;
        String jstackMaxLineStr = System.getProperty("dubbo.jstack-dump.max-line");
        if (StringUtils.isNotEmpty(jstackMaxLineStr)) {
            try {
                jstackMaxLine = Integer.parseInt(jstackMaxLineStr);
            } catch (Exception var12) {
            }
        }

        StackTraceElement[] stackTrace = threadInfo.getStackTrace();

        int var9;
        int var10;
        for(MonitorInfo[] lockedMonitors = threadInfo.getLockedMonitors(); i < stackTrace.length && i < jstackMaxLine; ++i) {
            StackTraceElement ste = stackTrace[i];
            sb.append("\tat ").append(ste.toString());
            sb.append('\n');
            if (i == 0 && threadInfo.getLockInfo() != null) {
                State ts = threadInfo.getThreadState();
                if (State.BLOCKED.equals(ts)) {
                    sb.append("\t-  blocked on ").append(threadInfo.getLockInfo());
                    sb.append('\n');
                } else if (State.WAITING.equals(ts) || State.TIMED_WAITING.equals(ts)) {
                    sb.append("\t-  waiting on ").append(threadInfo.getLockInfo());
                    sb.append('\n');
                }
            }

            MonitorInfo[] var14 = lockedMonitors;
            var9 = lockedMonitors.length;

            for(var10 = 0; var10 < var9; ++var10) {
                MonitorInfo mi = var14[var10];
                if (mi.getLockedStackDepth() == i) {
                    sb.append("\t-  locked ").append(mi);
                    sb.append('\n');
                }
            }
        }

        if (i < stackTrace.length) {
            sb.append("\t...");
            sb.append('\n');
        }

        LockInfo[] locks = threadInfo.getLockedSynchronizers();
        if (locks.length > 0) {
            sb.append("\n\tNumber of locked synchronizers = " + locks.length);
            sb.append('\n');
            LockInfo[] var15 = locks;
            var9 = locks.length;

            for(var10 = 0; var10 < var9; ++var10) {
                LockInfo li = var15[var10];
                sb.append("\t- " + li);
                sb.append('\n');
            }
        }

        sb.append('\n');
        return sb.toString();
    }
}

