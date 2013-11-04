package wad.template.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wad.template.domain.Departure;
import wad.template.domain.Line;
import wad.template.domain.LineInfo;
import wad.template.domain.Stop;

@Service
public class StopService {
    @Autowired
    private TimetableService timetableService;
    
    public List<Stop> searchStop(String query) {
        return timetableService.findStops(query);
    }
    
    public Stop getStop(Integer stopCode) {
        Stop stop = timetableService.getStop(stopCode);
        
        List<Line> lines = new ArrayList<Line>();
        for (LineInfo lineinfo : stop.getLineInfos()) {
            Line line = timetableService.getLine(lineinfo.getLineCode());
            if (line != null)
                lines.add(line);
        }
        
        stop.setLines(lines);
        
        if (stop.getDepartures() != null) {
            for (Departure departure : stop.getDepartures()) {
                departure.setLine(timetableService.getLine(departure.getLineCode()));
            }
        }
        return stop;
    }
    
    public List<Stop> getStops(List<Integer> stopcodes) {
        List<Stop> stops = new ArrayList<Stop>();

        for (Integer stopcode : stopcodes) {
            stops.add(this.getStop(stopcode));
        }

        return stops;
    }
    
    @Scheduled(fixedRate = 300000) //every 5 minutes
    @CacheEvict(value = "stops", allEntries = true)
    public void evictStopCache() {
//        System.out.println("Stop cache evicter called");
    }
}
