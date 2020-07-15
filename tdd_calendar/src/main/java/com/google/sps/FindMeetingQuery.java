// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    List<TimeRange> startRanges = events.stream()
        .filter(event -> {
          Set<String> requiredAttendees = new HashSet<>(event.getAttendees());
          requiredAttendees.retainAll(request.getAttendees());
          return requiredAttendees.size() > 0;
        })
        .map(Event::getWhen)
        .sorted(TimeRange.ORDER_BY_START)
        .collect(Collectors.toList());

    List<Integer> startTimes = startRanges.stream()
        .map(TimeRange::start)
        .collect(Collectors.toList());
    List<Integer> endTimes = startRanges.stream()
        .map(TimeRange::end)
        .sorted()
        .collect(Collectors.toList());

    List<TimeRange> availableTimes = new ArrayList<>();

    int availabilityStart = TimeRange.START_OF_DAY;
    int eventsInProgress = 0;

    while (startTimes.size() > 0 || endTimes.size() > 0) {
      if (startTimes.size() > 0 && startTimes.get(0) < endTimes.get(0)) {
        if (eventsInProgress == 0) {
          availableTimes.add(TimeRange.fromStartEnd(availabilityStart, startTimes.get(0), false));
        }
        // if start time > 0, guaranteed end time
        startTimes.remove(0);
        eventsInProgress++;
      } else {
        // either start = end, or start < end
        // either case process end
        eventsInProgress--;
        int endTime = endTimes.remove(0);
        if (eventsInProgress == 0) {
          availabilityStart = endTime;
        }
      }
    }

    // add from end to EOD
    availableTimes.add(TimeRange.fromStartEnd(availabilityStart, TimeRange.END_OF_DAY, true));

    return availableTimes.stream()
        .filter(timeRange -> timeRange.duration() >= request.getDuration())
        .collect(Collectors.toList());
  }
}
