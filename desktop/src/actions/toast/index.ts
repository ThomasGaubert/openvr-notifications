import keys from '../ActionTypeKeys';

export interface IToastShow {
  readonly type: keys.TOAST_SHOW;
  readonly payload: {
    readonly message: string;
    readonly type: string;
    readonly duration: number;
    readonly dismissable: boolean;
  };
}

export interface IToastHide {
  readonly type: keys.TOAST_HIDE;
}
