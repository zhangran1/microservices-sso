import { browser, ExpectedConditions as ec, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../../page-objects/jhi-page-objects';

import { ApponeComponentsPage, ApponeDeleteDialog, ApponeUpdatePage } from './appone.page-object';

const expect = chai.expect;

describe('Appone e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let apponeComponentsPage: ApponeComponentsPage;
  let apponeUpdatePage: ApponeUpdatePage;
  let apponeDeleteDialog: ApponeDeleteDialog;
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.loginWithOAuth(username, password);
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Appones', async () => {
    await navBarPage.goToEntity('appone');
    apponeComponentsPage = new ApponeComponentsPage();
    await browser.wait(ec.visibilityOf(apponeComponentsPage.title), 5000);
    expect(await apponeComponentsPage.getTitle()).to.eq('gatewayApp.applicationoneAppone.home.title');
    await browser.wait(ec.or(ec.visibilityOf(apponeComponentsPage.entities), ec.visibilityOf(apponeComponentsPage.noResult)), 1000);
  });

  it('should load create Appone page', async () => {
    await apponeComponentsPage.clickOnCreateButton();
    apponeUpdatePage = new ApponeUpdatePage();
    expect(await apponeUpdatePage.getPageTitle()).to.eq('gatewayApp.applicationoneAppone.home.createOrEditLabel');
    await apponeUpdatePage.cancel();
  });

  it('should create and save Appones', async () => {
    const nbButtonsBeforeCreate = await apponeComponentsPage.countDeleteButtons();

    await apponeComponentsPage.clickOnCreateButton();

    await promise.all([
      apponeUpdatePage.setNameInput('name'),
      apponeUpdatePage.setHandleInput('handle'),
      apponeUpdatePage.userSelectLastOption(),
    ]);

    await apponeUpdatePage.save();
    expect(await apponeUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await apponeComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Appone', async () => {
    const nbButtonsBeforeDelete = await apponeComponentsPage.countDeleteButtons();
    await apponeComponentsPage.clickOnLastDeleteButton();

    apponeDeleteDialog = new ApponeDeleteDialog();
    expect(await apponeDeleteDialog.getDialogTitle()).to.eq('gatewayApp.applicationoneAppone.delete.question');
    await apponeDeleteDialog.clickOnConfirmButton();
    await browser.wait(ec.visibilityOf(apponeComponentsPage.title), 5000);

    expect(await apponeComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
