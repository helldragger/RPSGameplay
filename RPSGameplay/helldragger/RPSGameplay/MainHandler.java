package helldragger.RPSGameplay;

import helldragger.RPSGameplay.IconMenu.OptionClickEvent;
import helldragger.RPSGameplay.IconMenu.OptionClickEventHandler;


class MainHandler implements OptionClickEventHandler{

	MainMenu main;
	
	MainHandler(MainMenu menu){
		this.main= menu;
	}

	@Override
	public void onOptionClick(OptionClickEvent event) {
		if(!event.willClose() & !event.willDestroy())
			event.setWillClose(true);
	}


}
