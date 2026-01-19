package com.cie.hr.infrastructure.bootstrap;

import com.cie.hr.infrastructure.entity.UnitsEntity;
import com.cie.hr.infrastructure.repository.UnitsJpaRepository;
import com.fasterxml.uuid.Generators;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author Alexis TAMBIE
 * @created 14/06/2023
 * @project hr-cie
 */
@Order(9)
@Component
public class UnitsBootstrapCommandLineRunner implements CommandLineRunner {

    private final UnitsJpaRepository unitsJpaRepository;

    public UnitsBootstrapCommandLineRunner(UnitsJpaRepository unitsJpaRepository) {
        this.unitsJpaRepository = unitsJpaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        var unitsList = new ArrayList<UnitsEntity>() {{
            var percent = new UnitsEntity("%", "Taux");
            percent.setId(Generators.timeBasedEpochGenerator().generate());
            add(percent);

            var unit = new UnitsEntity("U", "Nombre");
            unit.setId(Generators.timeBasedEpochGenerator().generate());
            add(unit);

            var teco = new UnitsEntity("TéCO2/GWh", "TéCO2/GWh");
            teco.setId(Generators.timeBasedEpochGenerator().generate());
            add(teco);

            var lGwh = new UnitsEntity("L/GWh", "Litre/GigaWattHeure");
            lGwh.setId(Generators.timeBasedEpochGenerator().generate());
            add(lGwh);

            var kf = new UnitsEntity("Kf", "KiloFrancs");
            kf.setId(Generators.timeBasedEpochGenerator().generate());
            add(kf);

            var date = new UnitsEntity("D", "Date");
            date.setId(Generators.timeBasedEpochGenerator().generate());
            add(date);

            var dc = new UnitsEntity("°C", "Degré Celsius");
            dc.setId(Generators.timeBasedEpochGenerator().generate());
            add(dc);

            var frs = new UnitsEntity("F", "Francs");
            frs.setId(Generators.timeBasedEpochGenerator().generate());
            add(frs);

            var gwh = new UnitsEntity("GWh", "GigaWattHeure");
            gwh.setId(Generators.timeBasedEpochGenerator().generate());
            add(gwh);

            var hour = new UnitsEntity("H", "Heure");
            hour.setId(Generators.timeBasedEpochGenerator().generate());
            add(hour);

            var jour = new UnitsEntity("J", "Jour");
            jour.setId(Generators.timeBasedEpochGenerator().generate());
            add(jour);

            var kg = new UnitsEntity("KG", "KiloGramme");
            kg.setId(Generators.timeBasedEpochGenerator().generate());
            add(kg);

            var kWh = new UnitsEntity("kWh", "KiloWattHeure");
            kWh.setId(Generators.timeBasedEpochGenerator().generate());
            add(kWh);

            var month = new UnitsEntity("M", "Mois");
            month.setId(Generators.timeBasedEpochGenerator().generate());
            add(month);

            var meter = new UnitsEntity("M3", "Mètre cube");
            meter.setId(Generators.timeBasedEpochGenerator().generate());
            add(meter);

            var mega = new UnitsEntity("Mwh", "MegaWattHeure");
            mega.setId(Generators.timeBasedEpochGenerator().generate());
            add(mega);

            var minute = new UnitsEntity("Min", "Minute");
            minute.setId(Generators.timeBasedEpochGenerator().generate());
            add(minute);

            var second = new UnitsEntity("S", "Seconde");
            second.setId(Generators.timeBasedEpochGenerator().generate());
            add(second);

            var litre = new UnitsEntity("L", "Litre");
            litre.setId(Generators.timeBasedEpochGenerator().generate());
            add(litre);

            var milliards = new UnitsEntity("Mds", "Milliards");
            milliards.setId(Generators.timeBasedEpochGenerator().generate());
            add(milliards);

            var million = new UnitsEntity("Mil", "Million");
            million.setId(Generators.timeBasedEpochGenerator().generate());
            add(million);

            var kilometre = new UnitsEntity("KM", "Kilomètre");
            kilometre.setId(Generators.timeBasedEpochGenerator().generate());
            add(kilometre);
        }};

        if (unitsJpaRepository.count() == 0) {
            unitsJpaRepository.saveAll(unitsList);
        }
    }
}
