import {FullCalendar} from '../full-calendar';

export class EpCalendar extends FullCalendar {
  _createInitOptions() {
    const options = super._createInitOptions();
    options.defaultView = 'timeGridThreeDays';

    options.views = {
      timeGridThreeDays: {
        type: 'timeGrid',
        dayCount: 3,
        weekNumbers: false
      }
    };

    options.firstDay = 1
    options.allDaySlot = false
    options.slotEventOverlap = true
    options.snapDuration = '00:15';

    return options;
  }
}

customElements.define('ep-calendar', EpCalendar);
