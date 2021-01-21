package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.Repository0;
import socialnetwork.repository.database.FriendrequestsDbRepository;
import socialnetwork.repository.database.MessageDbRepository;
import socialnetwork.repository.database.PrietenieDbRepository;
import socialnetwork.repository.database.UtilizatorDbRepository;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.repository.file.UtilizatorFile0;
import socialnetwork.service.MessageService;
import socialnetwork.service.PrietenieService;
import socialnetwork.service.RequestService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.UI;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //ui.run();
        MainFX.main(args);
    }
}


