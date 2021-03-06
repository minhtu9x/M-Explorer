class FileController < ApplicationController
  before_action :oauth_set, only: [:index, :open]
  def index
    @auth_url = @credentials.authorization_uri.to_s
    @credentials.refresh_token = params[:token]
    @credentials.fetch_access_token!
    session = GoogleDrive::Session.from_credentials(@credentials)

    temp = session.file_by_id(params[:id])
    if temp.mime_type == 'application/vnd.google-apps.folder'
      @files = temp.files
    else
      file = temp
      dir = File.dirname("#{Rails.root}/public/files/#{current_user.id}/file")
      FileUtils.mkdir_p(dir) unless File.directory?(dir)
      file.download_to_file("#{dir}/#{file.title}")
      url = "#{request.protocol + request.host + ':' + request.port.to_s}/files/#{current_user.id}/#{file.title}"
      redirect_to open_path(url: Base64.encode64(url))
   end
  end

  def upload(_folder = nil, access_token = nil, json = nil)
    @credentials = Google::Auth::UserRefreshCredentials.new(
      client_id: '389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com',
      client_secret: 'zhKkS-8vI_RNqReXOjAx4c5r',
      scope: [
        'https://www.googleapis.com/auth/drive'
      ],
      additional_parameters: {
        'access_type' => 'offline',
        'prompt' => 'consent',
        'include_granted_scopes' => 'true'

      },
      redirect_uri: "#{request.protocol + request.host + ':' + request.port.to_s}/googledrive_redirect")

    @credentials.refresh_token = access_token
    access_token = @credentials.fetch_access_token!['access_token']
    session = GoogleDrive::Session.from_credentials(@credentials)

    parent = session.file_by_id(_folder)

    json.each_with_index do |up, index|
      if index == 0
        parent = parent.create_subcollection(up['parent'])
      elsif up.is_a?(Array)
        upload(parent.id, access_token, up)
      else
        parent.upload_from_file(up['path'], up['name'], convert: false)
      end
    end

    aa
  end

  def open

    # HardWorker.perform_async
    # @user = User.find_by_id(1)
    # UserNotifier.regitration_confirmation(@user).deliver_later

    # a
    # a


    # a
    # session = Imgurapi::Session.instance(client_id: '1160021cb0066c5', client_secret: 'eb7c6fbed804ba86313942b249b8bcf399370da0', refresh_token: '2b2ce027db63e2d03fb953cca1479e13be0e9808')
    # client = Imgur.new('1160021cb0066c5')
    # d

    # @url = Base64.decode64(params[:url])

    #   dbx = Dropbox::Client.new("EMuhh18IKQAAAAAAAAAgelA7wkZ8OcvU2uXwTZzH0y-0GjI1NQXzvV2sv3ZLDfsF")
    #   @credentials.refresh_token = '1/w-fG1yj9f936nqUsGvj6UHbITfP64plO5ZLWAVPuuIE'
    #   access_token = @credentials.fetch_access_token!['access_token']
    # s
    #   session = GoogleDrive::Session.from_credentials(@credentials)
    #   root = session.root_collection.id.to_s
    #   json = '[
    #   {
    #     "parent": "root"
    #   },
    #   {
    #     "name": "1.png",
    #     "path": "/home/kyle/Project/M-Explorer/M-Web/public/a/root/1.png"
    #   },
    #   {
    #     "name": "2.png",
    #     "path": "/home/kyle/Project/M-Explorer/M-Web/public/a/root/2.png"
    #   },
    #   [
    #     {
    #       "parent": "sub"
    #     },
    #     {
    #       "name": "1.png",
    #       "path": "/home/kyle/Project/M-Explorer/M-Web/public/a/root/sub/1.png"
    #     },
    #     {
    #       "name": "2.png",
    #       "path": "/home/kyle/Project/M-Explorer/M-Web/public/a/root/sub/2.png"
    #     },
    #     [
    #       {
    #         "parent": "sub1"
    #       },
    #       {
    #         "name": "1.png",
    #         "path": "/home/kyle/Project/M-Explorer/M-Web/public/a/root/sub/1.png"
    #       },
    #       {
    #         "name": "2.png",
    #         "path": "/home/kyle/Project/M-Explorer/M-Web/public/a/root/sub/2.png"
    #       }
    #     ]
    #   ]
    #
    # ]'
    #   parsed_json = JSON.parse(json)
    #   upload('16V7aFG2FpixD8C06-RZPwQU4kSlqhMOd', access_token, parsed_json)
    # parsed_json.each do |a|
    #   if a.kind_of?(Array)
    #     i += 1
    #   end
    # end

    # render plain: parsed_json['refresh_token']

    # @credentials.refresh_token = '1/SKgXwN8NyQSJT5Ii6APhzY8HODR7OzrZ7LLC4P8NcqU'
    # @credentials.fetch_access_token!
    # session = GoogleDrive::Session.from_credentials(@credentials)
    # ese
    # temp = session.file_by_id('0ANNKtKq6pCEcUk9PVA')
    # d
    # page_token = nil
    # begin
    #   (files, page_token) = session.files(page_token: page_token)
    #   p files
    # end while page_token
    # @result = HTTParty.get('https://www.googleapis.com/drive/v2/about?access_token=' + access_token)
    # parsed_json = JSON.parse(@result.body)
    # gg

    # rf

    # render plain: ActionController::Base.helpers.asset_path('/images/default-avatar.png')

    # render plain: dir = "#{Rails.root}/public/files/1/JPG.jpg"
    # session.upload_from_file(dir,"aaa.aa")
    # dbx = Dropbox::Client.new("EMuhh18IKQAAAAAAAAAgiEEHMnyL_dc-kxnQZ6BT8TqtOaqpZFt8rw-iJ7xxuB8z")
    #
    # a = ''
    # files = dbx.list_folder("")
    # files.each do |f|
    #   a+= f.size
    # end
    #
    # aef
    # f

    # client = Boxr::Client.new('eAvtUsUWvzfYMPrJB7Hz0SR9I5zFEilU')
    # files = client.root_folder_items
    # fil = {}
    # files.each do |f|
    #   fil
    # end

  end

  private

  def oauth_set
    @credentials = Google::Auth::UserRefreshCredentials.new(
      client_id: ENV['google_client_id'],
      client_secret: ENV['google_client_secret'],
      scope: [
        'https://www.googleapis.com/auth/drive'
      ],
      additional_parameters: {
        'access_type' => 'offline',
        'prompt' => 'consent',
        'include_granted_scopes' => 'true'

      },
      redirect_uri: "#{request.protocol + request.host + ':' + request.port.to_s}/googledrive_redirect")
  end
end
