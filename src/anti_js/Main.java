package anti_js;

import arc.*;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.mod.*;
import mindustry.world.blocks.logic.MessageBlock;

public class Main extends Plugin {
    String bannedString = "runconsole(";

    void checkBlock(EventType.ConfigEvent blockUpdated) {
        if (blockUpdated.tile.block != Blocks.message) {
//            Log.info("Configured block was not a message block.");
            return;
        }
        if (!blockUpdated.value.toString().toLowerCase().contains(bannedString)) {
//            Log.info("Configured block did not contain malicious JS code.");
            return;
        }

        Log.info("A block with malicious JS code was placed. Kicking connection.");
        blockUpdated.player.kick("You cannot place message blocks containing malicious JS code.");
        blockUpdated.tile.kill();
    }

    void checkBlock(EventType.BlockBuildEndEvent placedBlock) {
        if (placedBlock.tile.block() != Blocks.message) {
            return;
        }
        if (!((MessageBlock.MessageBuild) placedBlock.tile.build).message.toString().toLowerCase().contains(bannedString)) {
            return;
        }

        Log.info("A block with malicious JS code was placed. Kicking connection.");
        placedBlock.unit.getPlayer().kick("You cannot place message blocks containing malicious JS code.");
        placedBlock.tile.build.kill();
    }

    public void init(){
        Events.on(EventType.ConfigEvent.class, this::checkBlock);
        Events.on(EventType.BlockBuildEndEvent.class, this::checkBlock);

        Vars.netServer.admins.addChatFilter((player, text) -> {
            if (text.toLowerCase().contains("runconsole")) {
                text = "The contents of this message was removed by a DDNS moderation plugin.";
            }

            return text;
        });
    }
}
