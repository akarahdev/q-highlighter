package dev.akarah.qh.client.data;

import dev.akarah.qh.client.MainClient;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;

import java.util.Arrays;
import java.util.Optional;

public class GetSweepData {
    public static boolean detectSweepDetailImpl(ClientboundSystemChatPacket par1) {
        if(par1.overlay()) {
            return true;
        }

        var component = par1.content().getString().trim().replace("  ", "");

        System.out.println("Component: " + component);
        System.out.println("Arr: " + Arrays.toString(component.split(" ")));

        if(component.startsWith("Sweep Details:")) {
            var split = component.split(" ");
            var sweepValue = Double.parseDouble(split[2].replace("∮", ""));
            MainClient.sweepData(new SweepData(
                    new SweepData.BaseValues(sweepValue, -1.0),
                    Optional.empty(),
                    Optional.empty(),
                    0
            ));
            return true;
        }
        if(component.startsWith("Fig Tree Toughness:") || component.startsWith("Mangrove Tree Toughness:")) {
            var sweepData = MainClient.sweepData();
            var split = component.split(" ");
            var toughness = Double.parseDouble(split[3]);
            var logs = Double.parseDouble(split[4]);
            MainClient.sweepData(new SweepData(
                    new SweepData.BaseValues(sweepData.baseValues().sweep(), toughness),
                    sweepData.axeThrowModifier(),
                    sweepData.styleModifier(),
                    logs
            ));
            return true;
        }
        if(component.startsWith("Axe throw:")) {
            var sweepData = MainClient.sweepData();
            var split = component.split(" ");
            var reduction = Double.parseDouble(split[2].replace("%", ""));
            var logs = Double.parseDouble(split[4]);
            MainClient.sweepData(new SweepData(
                    sweepData.baseValues(),
                    Optional.of(new SweepData.AxeThrow(reduction)),
                    sweepData.styleModifier(),
                    logs
            ));
            return true;
        }
        if(component.startsWith("Wrong Style:")) {
            var sweepData = MainClient.sweepData();
            var split = component.trim().split(" ");
            var reduction = Double.parseDouble(split[2].replace("%", ""));
            var logs = Double.parseDouble(split[4]);
            MainClient.sweepData(new SweepData(
                    sweepData.baseValues(),
                    sweepData.axeThrowModifier(),
                    Optional.of(new SweepData.WrongStyle("I don't know what the problem is :(", reduction)),
                    logs
            ));
            return true;
        }
        return false;
    }
}
