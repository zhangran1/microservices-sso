import * as dayjs from 'dayjs';
import { IAppone } from 'app/entities/applicationone/appone/appone.model';
import { ITag } from 'app/entities/applicationone/tag/tag.model';

export interface IPost {
  id?: number;
  title?: string;
  content?: string;
  date?: dayjs.Dayjs;
  applicationone?: IAppone | null;
  tags?: ITag[] | null;
}

export class Post implements IPost {
  constructor(
    public id?: number,
    public title?: string,
    public content?: string,
    public date?: dayjs.Dayjs,
    public applicationone?: IAppone | null,
    public tags?: ITag[] | null
  ) {}
}

export function getPostIdentifier(post: IPost): number | undefined {
  return post.id;
}
