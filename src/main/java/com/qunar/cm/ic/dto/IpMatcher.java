package com.qunar.cm.ic.dto;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by yu.qi on 2018/09/11.
 */

public class IpMatcher {
    private List<String> ips;
    private List<Pattern> patterns;

    public IpMatcher(List<String> ips) {
        this.ips = Lists.newArrayList(ips);
        this.patterns = ips.stream().map(Pattern::compile).collect(Collectors.toList());
    }

    public boolean match(String ip) {
        return ips.contains(ip.trim()) || patterns.stream().anyMatch(pattern -> pattern.matcher(ip).matches());
    }

    public List<String> getIps() {
        return ips;
    }
}
