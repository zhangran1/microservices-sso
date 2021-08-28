import { IUser } from 'app/entities/user/user.model';

export interface IAppone {
  id?: number;
  name?: string;
  handle?: string;
  user?: IUser | null;
}

export class Appone implements IAppone {
  constructor(public id?: number, public name?: string, public handle?: string, public user?: IUser | null) {}
}

export function getApponeIdentifier(appone: IAppone): number | undefined {
  return appone.id;
}
